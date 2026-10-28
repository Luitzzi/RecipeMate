package de.example.backend.dataschema;

import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeImageEntity;
import de.example.backend.entity.recipe.RecipeImageEntity.ImageUploadStatus;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.exception.image.InvalidImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static de.example.backend.dataschema.RecipeTestData.createRecipeUiDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeImageTest extends AbstractRecipeServiceTest {

    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;

    // Objects that exist in the mocked R2 bucket, by key
    private final Map<String, HeadObjectResponse> uploadedObjects = new HashMap<>();

    @BeforeEach
    void setUp() {
        mockR2HeadObject();
    }

    @Test
    void create_activatesUploadedImages() {
        uploadImage("image-a");
        uploadImage("image-b");
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("image-a", "image-b"));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(created.getImageKeys()).containsExactly("image-a", "image-b");
        RecipeEntity loaded = loadRecipe(created);
        assertThat(loaded.getImages())
                .extracting(RecipeImageEntity::getKey, RecipeImageEntity::getStatus,
                        image -> image.getRecipe().getId())
                .containsExactly(
                        tuple("image-a", ImageUploadStatus.ACTIVE, loaded.getId()),
                        tuple("image-b", ImageUploadStatus.ACTIVE, loaded.getId()));
    }

    @Test
    void create_keepsImageOrder() {
        // Persisted in a different order than requested to ensure the request order wins
        uploadImage("image-c");
        uploadImage("image-a");
        uploadImage("image-b");
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("image-a", "image-b", "image-c"));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        RecipeEntity loaded = loadRecipe(created);
        assertThat(loaded.getImages())
                .extracting(RecipeImageEntity::getKey, RecipeImageEntity::getPosition)
                .containsExactly(tuple("image-a", 0), tuple("image-b", 1), tuple("image-c", 2));
        assertThat(loaded.getCoverImage())
                .map(RecipeImageEntity::getKey)
                .contains("image-a");
    }

    @Test
    void create_rejectsDuplicateImageKeys() {
        uploadImage("image-a");
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("image-a", "image-a"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_rejectsUnknownImageKey() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("unknown"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class);
        verify(s3Client, never()).headObject(any(Consumer.class));
    }

    @Test
    void create_rejectsImageOfOtherUser() {
        UserEntity otherUser = new UserEntity();
        entityManager.persist(otherUser);
        uploadImage("foreign-image", otherUser, ImageUploadStatus.RESERVED, "image/png", 1_000L);
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("foreign-image"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class);
        verify(s3Client, never()).headObject(any(Consumer.class));
    }

    @Test
    void create_rejectsAlreadyActiveImage() {
        uploadImage("active-image", owner, ImageUploadStatus.ACTIVE, "image/png", 1_000L);
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("active-image"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class);
    }

    @Test
    void create_rejectsImageNotUploadedToR2() {
        reserveImage("reserved-only");
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("reserved-only"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("not uploaded");
    }

    @Test
    void create_rejectsImageWithInvalidContentType() {
        uploadImage("pdf-image", owner, ImageUploadStatus.RESERVED, "application/pdf", 1_000L);
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("pdf-image"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("application/pdf");
    }

    @Test
    void create_rejectsImageExceedingMaxSize() {
        uploadImage("huge-image", owner, ImageUploadStatus.RESERVED, "image/png", MAX_IMAGE_SIZE_BYTES + 1);
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("huge-image"));

        assertThatThrownBy(() -> recipeService.create(recipe, owner))
                .isInstanceOf(InvalidImageException.class)
                .hasMessageContaining("maximum size");
    }

    @Test
    void create_acceptsImageWithMaxSize() {
        uploadImage("max-image", owner, ImageUploadStatus.RESERVED, "image/png", MAX_IMAGE_SIZE_BYTES);
        dataSchemaTestUtils.flushAndClear();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setImageKeys(List.of("max-image"));

        RecipeUiDTO created = recipeService.create(recipe, owner);

        assertThat(created.getImageKeys()).containsExactly("max-image");
    }

    /**
     * Answers HeadObject requests from {@link #uploadedObjects}
     * and throws a 404 like R2 for every other key.
     */
    @SuppressWarnings("unchecked")
    private void mockR2HeadObject() {
        when(s3Client.headObject(any(Consumer.class))).thenAnswer(invocation -> {
            Consumer<HeadObjectRequest.Builder> buildRequest = invocation.getArgument(0);
            HeadObjectRequest.Builder requestBuilder = HeadObjectRequest.builder();
            buildRequest.accept(requestBuilder);
            String key = requestBuilder.build().key();

            HeadObjectResponse response = uploadedObjects.get(key);
            if (response == null)
                throw NoSuchKeyException.builder().statusCode(404).message("Not found").build();
            return response;
        });
    }

    /**
     * Reserves an image in the db without uploading it to R2.
     */
    private void reserveImage(String key) {
        entityManager.persist(new RecipeImageEntity(owner, Instant.now(), key, ImageUploadStatus.RESERVED));
    }

    /**
     * Reserves a valid image for the owner and uploads it to the mocked R2.
     */
    private void uploadImage(String key) {
        uploadImage(key, owner, ImageUploadStatus.RESERVED, "image/png", 1_000L);
    }

    private void uploadImage(
            String key, UserEntity createdBy, ImageUploadStatus status,
            String contentType, long contentLength) {
        entityManager.persist(new RecipeImageEntity(createdBy, Instant.now(), key, status));
        uploadedObjects.put(key, HeadObjectResponse.builder()
                .contentType(contentType)
                .contentLength(contentLength)
                .build());
    }
}
