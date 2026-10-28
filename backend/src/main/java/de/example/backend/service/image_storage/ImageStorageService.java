package de.example.backend.service.image_storage;

import de.example.backend.boundary.dto.image_storage.ImageUrlUiDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    @Value("${r2.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Duration UPLOAD_TTL = Duration.ofMinutes(5);
    private static final Duration DOWNLOAD_TTL = Duration.ofMinutes(15);

    private final S3Client s3Client;
    private final S3Presigner presigner;

    public ImageUrlUiDTO createUploadUrl(String contentType, UUID userId) {
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Content type %s is not allowed".formatted(contentType));
        }

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
        return new ImageUrlUiDTO(key, presignedUploadURL);
    }

    public ImageUrlUiDTO createDownloadUrl(String key) {
        GetObjectPresignRequest getRequest = GetObjectPresignRequest.builder()
                .signatureDuration(DOWNLOAD_TTL)
                .getObjectRequest(request -> request
                        .bucket(bucketName)
                        .key(key))
                .build();
        URL presignedDownloadURL = presigner.presignGetObject(getRequest).url();
        return new ImageUrlUiDTO(key, presignedDownloadURL);
    }
}
