package de.example.backend.folder;

import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FolderRecipeAssignmentTest extends AbstractFolderTest {

    private static final long UNKNOWN_ID = -1L;

    private Long folderId;
    private Long recipeId;

    @BeforeEach
    void setUp() {
        folderId = createFolder("Desserts", owner).getId();
        recipeId = createRecipe("Cake", owner);
        dataSchemaTestUtils.flushAndClear();
    }

    // ---------- addRecipe ----------

    @Test
    void addRecipe_addsRecipeToFolder() {
        folderService.addRecipe(folderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).containsExactly(recipeId);
    }

    @Test
    void addRecipe_isIdempotent() {
        folderService.addRecipe(folderId, recipeId, owner);
        dataSchemaTestUtils.flushAndClear();
        folderService.addRecipe(folderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).containsExactly(recipeId);
    }

    @Test
    void addRecipe_allowsRecipeInMultipleFolders() {
        Long secondFolderId = createFolder("Favorites", owner).getId();
        dataSchemaTestUtils.flushAndClear();

        folderService.addRecipe(folderId, recipeId, owner);
        folderService.addRecipe(secondFolderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).containsExactly(recipeId);
        assertThat(recipeIdsInFolder(secondFolderId)).containsExactly(recipeId);
    }

    @Test
    void addRecipe_rejectsUnknownFolder() {
        assertThatThrownBy(() -> folderService.addRecipe(UNKNOWN_ID, recipeId, owner))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addRecipe_rejectsUnknownRecipe() {
        assertThatThrownBy(() -> folderService.addRecipe(folderId, UNKNOWN_ID, owner))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addRecipe_rejectsFolderOfOtherUser() {
        Long foreignFolderId = createFolder("Foreign", otherUser).getId();
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() -> folderService.addRecipe(foreignFolderId, recipeId, owner))
                .isInstanceOf(NoAccessException.class);
        assertThat(recipeIdsInFolder(foreignFolderId)).isEmpty();
    }

    @Test
    void addRecipe_rejectsRecipeOfOtherUser() {
        Long foreignRecipeId = createRecipe("Foreign cake", otherUser);
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() -> folderService.addRecipe(folderId, foreignRecipeId, owner))
                .isInstanceOf(NoAccessException.class);
        assertThat(recipeIdsInFolder(folderId)).isEmpty();
    }

    // ---------- removeRecipe ----------

    @Test
    void removeRecipe_removesRecipeFromFolderButKeepsRecipe() {
        Long otherRecipeId = createRecipe("Pie", owner);
        folderService.addRecipe(folderId, recipeId, owner);
        folderService.addRecipe(folderId, otherRecipeId, owner);
        dataSchemaTestUtils.flushAndClear();

        folderService.removeRecipe(folderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).containsExactly(otherRecipeId);
        assertThat(recipeRepository.findById(recipeId)).isPresent();
    }

    @Test
    void removeRecipe_onlyAffectsGivenFolder() {
        Long secondFolderId = createFolder("Favorites", owner).getId();
        folderService.addRecipe(folderId, recipeId, owner);
        folderService.addRecipe(secondFolderId, recipeId, owner);
        dataSchemaTestUtils.flushAndClear();

        folderService.removeRecipe(folderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).isEmpty();
        assertThat(recipeIdsInFolder(secondFolderId)).containsExactly(recipeId);
    }

    @Test
    void removeRecipe_notInFolderIsNoOp() {
        folderService.removeRecipe(folderId, recipeId, owner);

        assertThat(recipeIdsInFolder(folderId)).isEmpty();
    }

    @Test
    void removeRecipe_rejectsUnknownFolder() {
        assertThatThrownBy(() -> folderService.removeRecipe(UNKNOWN_ID, recipeId, owner))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void removeRecipe_rejectsUnknownRecipe() {
        assertThatThrownBy(() -> folderService.removeRecipe(folderId, UNKNOWN_ID, owner))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void removeRecipe_rejectsFolderOfOtherUser() {
        Long foreignFolderId = createFolder("Foreign", otherUser).getId();
        Long foreignRecipeId = createRecipe("Foreign cake", otherUser);
        folderService.addRecipe(foreignFolderId, foreignRecipeId, otherUser);
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() -> folderService.removeRecipe(foreignFolderId, foreignRecipeId, owner))
                .isInstanceOf(NoAccessException.class);
        assertThat(recipeIdsInFolder(foreignFolderId)).containsExactly(foreignRecipeId);
    }

    // ---------- cascading ----------

    @Test
    void deletingRecipe_removesItFromFolder() {
        folderService.addRecipe(folderId, recipeId, owner);
        dataSchemaTestUtils.flushAndClear();

        recipeRepository.deleteById(recipeId);

        assertThat(recipeIdsInFolder(folderId)).isEmpty();
    }
}
