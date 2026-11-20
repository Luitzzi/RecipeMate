package de.example.backend.folder;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.recipe.filter.RecipeFilterUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.recipe.RecipeTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FolderRecipeFilterTest extends AbstractFolderTest {

    private Long dessertsId;
    private Long emptyFolderId;
    private Long cakeId;
    private Long pieId;
    private Long soupId;

    @BeforeEach
    void setUp() {
        dessertsId = createFolder("Desserts", owner).getId();
        emptyFolderId = createFolder("Empty", owner).getId();
        cakeId = createRecipe("Cake", owner);
        pieId = createRecipe("Pie", owner);
        soupId = createRecipe("Soup", owner);
        folderService.addRecipe(dessertsId, cakeId, owner);
        folderService.addRecipe(dessertsId, pieId, owner);
        dataSchemaTestUtils.flushAndClear();
    }

    @Test
    void filter_withoutFolderReturnsAllRecipes() {
        assertThat(filterIds(newFilter())).containsExactlyInAnyOrder(cakeId, pieId, soupId);
    }

    @Test
    void filter_byFolderReturnsOnlyRecipesInFolder() {
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(dessertsId);

        assertThat(filterIds(filter)).containsExactlyInAnyOrder(cakeId, pieId);
    }

    @Test
    void filter_byFolderCountsOnlyRecipesInFolder() {
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(dessertsId);

        PageUiDTO<RecipeUiDTO> page = recipeService.filter(filter, owner, PageRequest.of(0, 1));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filter_byEmptyFolderReturnsNothing() {
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(emptyFolderId);

        assertThat(filterIds(filter)).isEmpty();
    }

    @Test
    void filter_byFolderReflectsRemovedRecipe() {
        folderService.removeRecipe(dessertsId, pieId, owner);
        dataSchemaTestUtils.flushAndClear();
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(dessertsId);

        assertThat(filterIds(filter)).containsExactly(cakeId);
    }

    @Test
    void filter_byFolderCombinesWithOtherCriteria() {
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(dessertsId);
        filter.setTitle("Pie");

        assertThat(filterIds(filter)).containsExactly(pieId);
    }

    @Test
    void filter_byFolderOfOtherUserReturnsNothing() {
        Long foreignFolderId = createFolder("Foreign", otherUser).getId();
        Long foreignRecipeId = createRecipe("Foreign cake", otherUser);
        folderService.addRecipe(foreignFolderId, foreignRecipeId, otherUser);
        dataSchemaTestUtils.flushAndClear();
        RecipeFilterUiDTO filter = newFilter();
        filter.setFolderId(foreignFolderId);

        assertThat(filterIds(filter)).isEmpty();
    }

    private RecipeFilterUiDTO newFilter() {
        RecipeFilterUiDTO filter = new RecipeFilterUiDTO();
        filter.setLanguage(RecipeTestData.LANGUAGE);
        return filter;
    }

    private List<Long> filterIds(RecipeFilterUiDTO filter) {
        return recipeService.filter(filter, owner, PageRequest.of(0, 20)).getContent().stream()
                .map(RecipeUiDTO::getId)
                .toList();
    }
}
