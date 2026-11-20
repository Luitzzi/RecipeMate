package de.example.backend.folder;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.folder.FolderUiDTO;
import de.example.backend.entity.folder.FolderEntity;
import de.example.backend.entity.image.ImageEntity;
import de.example.backend.entity.image.ImageEntity.ImageUploadStatus;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.Instant;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import de.example.backend.service.exception.AlreadyExistsException;
import de.example.backend.service.exception.NoAccessException;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FolderServiceTest extends AbstractFolderTest {

    private static final long UNKNOWN_ID = -1L;

    // ---------- create ----------

    @Test
    void create_persistsFolderForOwner() {
        FolderUiDTO created = createFolder("Desserts", owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Desserts");
        FolderEntity loaded = folderRepository.findById(created.getId()).orElseThrow();
        assertThat(loaded.getName()).isEqualTo("Desserts");
        assertThat(loaded.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(loaded.getRecipes()).isEmpty();
    }

    @Test
    void create_rejectsDuplicateNameOfSameOwner() {
        createFolder("Desserts", owner);

        assertThatThrownBy(() -> createFolder("Desserts", owner))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void create_allowsSameNameForDifferentOwners() {
        FolderUiDTO ownFolder = createFolder("Desserts", owner);
        FolderUiDTO otherFolder = createFolder("Desserts", otherUser);

        assertThat(otherFolder.getId()).isNotEqualTo(ownFolder.getId());
    }

    // ---------- get ----------

    @Test
    void get_returnsOwnFolder() {
        FolderUiDTO created = createFolder("Desserts", owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderService.get(created.getId(), owner))
                .get()
                .extracting(FolderUiDTO::getName)
                .isEqualTo("Desserts");
    }

    @Test
    void get_returnsEmptyForUnknownFolder() {
        assertThat(folderService.get(UNKNOWN_ID, owner)).isEmpty();
    }

    @Test
    void get_rejectsFolderOfOtherUser() {
        FolderUiDTO created = createFolder("Desserts", otherUser);

        assertThatThrownBy(() -> folderService.get(created.getId(), owner))
                .isInstanceOf(NoAccessException.class);
    }

    // ---------- getPage ----------

    @Test
    void getPage_returnsOnlyOwnFoldersSortedByName() {
        createFolder("Pasta", owner);
        createFolder("Breakfast", owner);
        createFolder("Desserts", owner);
        createFolder("Foreign", otherUser);
        dataSchemaTestUtils.flushAndClear();

        PageUiDTO<FolderUiDTO> page = folderService.getPage(owner, PageRequest.of(0, 20));

        assertThat(page.getContent())
                .extracting(FolderUiDTO::getName)
                .containsExactly("Breakfast", "Desserts", "Pasta");
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void getPage_paginates() {
        createFolder("A", owner);
        createFolder("B", owner);
        createFolder("C", owner);
        dataSchemaTestUtils.flushAndClear();

        PageUiDTO<FolderUiDTO> secondPage = folderService.getPage(owner, PageRequest.of(1, 2));

        assertThat(secondPage.getContent())
                .extracting(FolderUiDTO::getName)
                .containsExactly("C");
        assertThat(secondPage.getTotalElements()).isEqualTo(3);
        assertThat(secondPage.getTotalPages()).isEqualTo(2);
    }

    // ---------- update ----------

    @Test
    void update_renamesFolder() {
        FolderUiDTO created = createFolder("Desserts", owner);
        dataSchemaTestUtils.flushAndClear();

        created.setName("Sweets");
        assertThat(folderService.update(created, owner))
                .get()
                .extracting(FolderUiDTO::getName)
                .isEqualTo("Sweets");
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderRepository.findById(created.getId()).orElseThrow().getName()).isEqualTo("Sweets");
    }

    @Test
    void update_keepingSameNameSucceeds() {
        FolderUiDTO created = createFolder("Desserts", owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderService.update(created, owner))
                .get()
                .extracting(FolderUiDTO::getName)
                .isEqualTo("Desserts");
    }

    @Test
    void update_rejectsNameOfOtherOwnFolder() {
        createFolder("Desserts", owner);
        FolderUiDTO pasta = createFolder("Pasta", owner);
        dataSchemaTestUtils.flushAndClear();

        pasta.setName("Desserts");
        assertThatThrownBy(() -> folderService.update(pasta, owner))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void update_returnsEmptyForUnknownFolder() {
        FolderUiDTO unknown = new FolderUiDTO();
        unknown.setId(UNKNOWN_ID);
        unknown.setName("Desserts");

        assertThat(folderService.update(unknown, owner)).isEmpty();
    }

    @Test
    void update_rejectsFolderOfOtherUser() {
        FolderUiDTO foreign = createFolder("Desserts", otherUser);
        dataSchemaTestUtils.flushAndClear();

        foreign.setName("Hijacked");
        assertThatThrownBy(() -> folderService.update(foreign, owner))
                .isInstanceOf(NoAccessException.class);
    }

    @Test
    void update_activatesNewImage() {
        FolderUiDTO folder = createFolder("Desserts", owner);
        uploadImage("image-a");
        dataSchemaTestUtils.flushAndClear();

        folder.setImageKey("image-a");
        folderService.update(folder, owner);
        dataSchemaTestUtils.flushAndClear();

        ImageEntity image = folderRepository.findById(folder.getId()).orElseThrow().getImage();
        assertThat(image.getKey()).isEqualTo("image-a");
        assertThat(image.getStatus()).isEqualTo(ImageUploadStatus.ACTIVE);
    }

    @Test
    void update_keepsUnchangedImage() {
        FolderUiDTO folder = createFolderWithImage("Desserts", "image-a");
        folder.setName("Cakes");

        assertThat(folderService.update(folder, owner))
                .get()
                .extracting(FolderUiDTO::getImageKey)
                .isEqualTo("image-a");
    }

    @Test
    void update_removesImage() {
        FolderUiDTO folder = createFolderWithImage("Desserts", "image-a");
        folder.setImageKey(null);

        folderService.update(folder, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderRepository.findById(folder.getId()).orElseThrow().getImage()).isNull();
    }

    // ---------- delete ----------

    @Test
    void delete_removesFolderButKeepsItsRecipes() {
        FolderUiDTO folder = createFolder("Desserts", owner);
        Long recipeId = createRecipe("Cake", owner);
        folderService.addRecipe(folder.getId(), recipeId, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderService.delete(folder.getId(), owner)).isPresent();
        dataSchemaTestUtils.flushAndClear();

        assertThat(folderRepository.findById(folder.getId())).isEmpty();
        assertThat(recipeRepository.findById(recipeId)).isPresent();
    }

    @Test
    void delete_returnsEmptyForUnknownFolder() {
        assertThat(folderService.delete(UNKNOWN_ID, owner)).isEmpty();
    }

    @Test
    void delete_rejectsFolderOfOtherUser() {
        FolderUiDTO foreign = createFolder("Desserts", otherUser);
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() -> folderService.delete(foreign.getId(), owner))
                .isInstanceOf(NoAccessException.class);
        dataSchemaTestUtils.flushAndClear();
        assertThat(folderRepository.findById(foreign.getId())).isPresent();
    }
    private FolderUiDTO createFolderWithImage(String name, String imageKey) {
        uploadImage(imageKey);
        FolderUiDTO folder = new FolderUiDTO();
        folder.setName(name);
        folder.setImageKey(imageKey);
        FolderUiDTO created = folderService.create(folder, owner);
        dataSchemaTestUtils.flushAndClear();
        return created;
    }

    /**
     * Reserves an image for the owner and mocks every S3 HeadObject request as a valid upload.
     */
    @SuppressWarnings("unchecked")
    private void uploadImage(String key) {
        entityManager.persist(new ImageEntity(owner, Instant.now(), key, ImageUploadStatus.RESERVED));
        when(s3Client.headObject(any(Consumer.class))).thenReturn(HeadObjectResponse.builder()
                .contentType("image/png")
                .contentLength(1_000L)
                .build());
    }
}
