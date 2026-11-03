package de.example.backend.service.image_storage;

import de.example.backend.boundary.dto.image_storage.ImageUrlUiDTO;
import de.example.backend.entity.recipe.RecipeImageEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.RecipeImageRepository;
import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.image.InvalidImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    @Value("${r2.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Duration UPLOAD_TTL = Duration.ofMinutes(5);
    private static final Duration DOWNLOAD_TTL = Duration.ofMinutes(15);
    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;
    private static final int IMAGE_DELETION_MAX_BATCH_SIZE = 1000;

    private final RecipeImageRepository recipeImageRepository;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public ImageUrlUiDTO createUploadUrl(String contentType, UserEntity user) {
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Content type %s is not allowed".formatted(contentType));
        }

        UUID userId = user.getId();
        String key = "users/%s/%s".formatted(userId, UUID.randomUUID());

        PutObjectPresignRequest putRequest = PutObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_TTL)
                .putObjectRequest(request -> request
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType))
                .build();
        URL presignedUploadURL = presigner.presignPutObject(putRequest)
                .url();
        persistReservedImage(key, user);
        return new ImageUrlUiDTO(key, presignedUploadURL.toString());
    }

    public List<RecipeImageEntity> loadImages(
            List<String> keys, UserEntity user
    ) {
        if (new HashSet<>(keys).size() != keys.size())
            throw new IllegalArgumentException("Duplicate image keys");

        Set<RecipeImageEntity> images = recipeImageRepository
                .findAllByKeyInAndCreatedByAndStatus(
                        keys, user,
                        RecipeImageEntity.ImageUploadStatus.RESERVED);
        if (keys.size() != images.size())
            throw new InvalidImageException("Image[s] in the request does not exist or do not belong to the user");

        keys.forEach(this::verifyUploaded);
        return orderImages(keys, images);
    }

    private void verifyUploaded(String key) {
        HeadObjectResponse head;
        try {
            head = s3Client.headObject(request -> request
                    .bucket(bucketName)
                    .key(key));
        } catch (S3Exception e) {
            if (e.statusCode() == HttpStatus.NOT_FOUND.value())
                throw new InvalidImageException("Image %s was not uploaded".formatted(key));
            throw e;
        }

        if (!ALLOWED_TYPES.contains(head.contentType()))
            throw new InvalidImageException("Content type %s of image %s is not allowed".formatted(head.contentType(), key));
        if (head.contentLength() > MAX_IMAGE_SIZE_BYTES)
            throw new InvalidImageException("Image %s exceeds the maximum size".formatted(key));
    }

    private List<RecipeImageEntity> orderImages(
            List<String> orderedKeys,
            Set<RecipeImageEntity> images
    ) {
        Map<String, RecipeImageEntity> imagesByKey = images.stream()
                .collect(Collectors.toMap(
                        RecipeImageEntity::getKey,
                        Function.identity())
                );
        return orderedKeys.stream()
                .map(imagesByKey::get)
                .toList();
    }

    public ImageUrlUiDTO createDownloadUrl(String key, UserEntity user) {
        if (!doesUserOwnKey(key, user)) {
            throw new NoAccessException("image");
        }

        GetObjectPresignRequest getRequest = GetObjectPresignRequest.builder()
                .signatureDuration(DOWNLOAD_TTL)
                .getObjectRequest(request -> request
                        .bucket(bucketName)
                        .key(key))
                .build();
        URL presignedDownloadURL = presigner.presignGetObject(getRequest).url();
        return new ImageUrlUiDTO(key, presignedDownloadURL.toString());
    }

    private void persistReservedImage(String key, UserEntity user) {
        RecipeImageEntity reservedImage = new RecipeImageEntity(
                user, Instant.now(), key, RecipeImageEntity.ImageUploadStatus.RESERVED
        );
        recipeImageRepository.persist(reservedImage);
    }

    private boolean doesUserOwnKey(String key, UserEntity user) {
        return recipeImageRepository.existsByKeyAndCreatedBy(key, user);
    }

    /**
     * Delete all expired images (images with status RESERVED and createdAt longer than 24 hours in the past)
     * every Monday at 6 am.
     */
    @Scheduled(cron = "0 0 6 * * Mon", zone = "Europe/Berlin")
    void imageGarbageCollector() {
        List<RecipeImageEntity> expiredImages = recipeImageRepository
                .findAllByCreatedAtBeforeAndStatus(
                        Instant.now().minus(Duration.ofHours(24)),
                        RecipeImageEntity.ImageUploadStatus.RESERVED);
        if (expiredImages.isEmpty()) {
            log.info("Image Garbage-Collector: No images to delete");
            return;
        }

        log.info("Image Garbage-Collector: Deleting expired images: {}", expiredImages.stream().map(RecipeImageEntity::getKey).toList());
        for (int i = 0; i < expiredImages.size(); i += IMAGE_DELETION_MAX_BATCH_SIZE) {
            List<RecipeImageEntity> chunk = expiredImages
                    .subList(
                            i,
                            Math.min(
                                    expiredImages.size(),
                                    i + IMAGE_DELETION_MAX_BATCH_SIZE));
            deleteImages(chunk);
        }
    }

    private void deleteImages(List<RecipeImageEntity> images) {
        List<ObjectIdentifier> ids = images.stream()
                .map(image -> ObjectIdentifier.builder().key(image.getKey()).build())
                .toList();

        DeleteObjectsResponse deletionResponse = s3Client.deleteObjects(requestBuilder -> requestBuilder
                .bucket(bucketName)
                .delete(delete -> delete
                        .objects(ids)));

        List<RecipeImageEntity> deleted = images;
        if (deletionResponse.hasErrors()) {
            Set<String> errorKeys = deletionResponse.errors().stream()
                    .map(S3Error::key)
                    .collect(Collectors.toSet());

            log.error("""
                    Failed to delete images.
                    Failed keys: {},
                    Errors: {}
                    """, errorKeys, deletionResponse.errors());

            deleted = images.stream()
                    .filter(image -> !errorKeys.contains(image.getKey()))
                    .toList();
        }
        recipeImageRepository.deleteAll(deleted);
    }
}
