package de.example.backend.recipe;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.feed.FeedSectionType;
import de.example.backend.boundary.dto.feed.FeedSectionUiDTO;
import de.example.backend.boundary.dto.feed.RecipeSummaryUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.image.ImageEntity;
import de.example.backend.entity.image.ImageEntity.ImageUploadStatus;
import de.example.backend.entity.recipe.RecipeProperty;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.FeedService;
import de.example.backend.service.exception.feed.InvalidFeedSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static de.example.backend.recipe.RecipeTestData.createRecipeUiDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FeedServiceTest extends AbstractRecipeServiceTest {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private FeedService feedService;

    @BeforeEach
    void setUp() {
        // Every image key exists in the mocked S3 bucket
        when(s3Client.headObject(any(Consumer.class))).thenAnswer(invocation -> HeadObjectResponse.builder()
                .contentType("image/png")
                .contentLength(1_000L)
                .build());
    }

    @Test
    void getSections_withoutRecipes_returnsNoSections() {
        assertThat(feedService.getSections(owner, PAGE_SIZE)).isEmpty();
    }

    @Test
    void getSections_omitsEmptySectionsAndKeepsFeedOrder() {
        createRecipe("Cake", RecipeProperty.DESSERT);
        createRecipe("Porridge", RecipeProperty.BREAKFAST);
        dataSchemaTestUtils.flushAndClear();

        List<FeedSectionUiDTO> sections = feedService.getSections(owner, PAGE_SIZE);

        assertThat(sections)
                .extracting(FeedSectionUiDTO::getType, FeedSectionUiDTO::getProperty)
                .containsExactly(
                        tuple(FeedSectionType.RECENT, null),
                        tuple(FeedSectionType.PROPERTY, RecipeProperty.BREAKFAST),
                        tuple(FeedSectionType.PROPERTY, RecipeProperty.DESSERT));
        assertThat(sections.get(1).getRecipes().getContent())
                .extracting(RecipeSummaryUiDTO::getTitle)
                .containsExactly("Porridge");
    }

    @Test
    void recentSection_isOrderedFromNewestToOldest() {
        createRecipe("First");
        createRecipe("Second");
        createRecipe("Third");
        dataSchemaTestUtils.flushAndClear();

        PageUiDTO<RecipeSummaryUiDTO> recent = getSectionPage(FeedSectionType.RECENT, null, 0, PAGE_SIZE);

        assertThat(recent.getContent())
                .extracting(RecipeSummaryUiDTO::getTitle)
                .containsExactly("Third", "Second", "First");
    }

    @Test
    void getSectionPage_pagesThroughTheRecipesOfTheSection() {
        createRecipe("Quick 1", RecipeProperty.QUICK);
        createRecipe("Quick 2", RecipeProperty.QUICK);
        createRecipe("Quick 3", RecipeProperty.QUICK);
        createRecipe("Not quick", RecipeProperty.DESSERT);
        dataSchemaTestUtils.flushAndClear();

        PageUiDTO<RecipeSummaryUiDTO> first = getSectionPage(FeedSectionType.PROPERTY, RecipeProperty.QUICK, 0, 2);
        PageUiDTO<RecipeSummaryUiDTO> second = getSectionPage(FeedSectionType.PROPERTY, RecipeProperty.QUICK, 1, 2);

        assertThat(first.getTotalElements()).isEqualTo(3);
        assertThat(first.getTotalPages()).isEqualTo(2);
        assertThat(first.getContent()).extracting(RecipeSummaryUiDTO::getTitle)
                .containsExactly("Quick 3", "Quick 2");
        assertThat(second.getContent()).extracting(RecipeSummaryUiDTO::getTitle)
                .containsExactly("Quick 1");
    }

    @Test
    void getSections_containsOnlyRecipesOfTheOwner() {
        UserEntity otherUser = new UserEntity();
        entityManager.persist(otherUser);
        RecipeUiDTO foreignRecipe = createRecipeUiDTO();
        foreignRecipe.setTitle("Foreign");
        recipeService.create(foreignRecipe, otherUser);
        createRecipe("Own");
        dataSchemaTestUtils.flushAndClear();

        List<FeedSectionUiDTO> sections = feedService.getSections(owner, PAGE_SIZE);

        assertThat(sections).singleElement()
                .satisfies(section -> assertThat(section.getRecipes().getContent())
                        .extracting(RecipeSummaryUiDTO::getTitle)
                        .containsExactly("Own"));
    }

    @Test
    void summary_hasCoverImageUrlOfFirstImageOrNull() {
        uploadImage("image-first");
        uploadImage("image-second");
        RecipeUiDTO withImages = createRecipeUiDTO();
        withImages.setTitle("With images");
        withImages.setImageKeys(List.of("image-first", "image-second"));
        recipeService.create(withImages, owner);
        createRecipe("Without image");
        dataSchemaTestUtils.flushAndClear();

        PageUiDTO<RecipeSummaryUiDTO> recent = getSectionPage(FeedSectionType.RECENT, null, 0, PAGE_SIZE);

        // One row per recipe: the image join must not duplicate recipes
        assertThat(recent.getTotalElements()).isEqualTo(2);
        assertThat(recent.getContent()).hasSize(2);
        RecipeSummaryUiDTO withoutImageSummary = recent.getContent().get(0);
        RecipeSummaryUiDTO withImagesSummary = recent.getContent().get(1);
        assertThat(withoutImageSummary.getCoverImageUrl()).isNull();
        assertThat(withImagesSummary.getCoverImageUrl())
                .contains("image-first")
                .doesNotContain("image-second");
    }

    @Test
    void getSectionPage_propertySectionWithoutProperty_throws() {
        assertThatThrownBy(() -> getSectionPage(FeedSectionType.PROPERTY, null, 0, PAGE_SIZE))
                .isInstanceOf(InvalidFeedSectionException.class);
    }

    @Test
    void getSectionPage_recentSectionWithProperty_throws() {
        assertThatThrownBy(() -> getSectionPage(FeedSectionType.RECENT, RecipeProperty.QUICK, 0, PAGE_SIZE))
                .isInstanceOf(InvalidFeedSectionException.class);
    }

    private PageUiDTO<RecipeSummaryUiDTO> getSectionPage(FeedSectionType type, RecipeProperty property,
            int page, int size) {
        return feedService.getSectionPage(type, property, owner, PageRequest.of(page, size));
    }

    private void createRecipe(String title, RecipeProperty... properties) {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setTitle(title);
        recipe.getProperties().clear();
        recipe.getProperties().addAll(List.of(properties));
        recipeService.create(recipe, owner);
    }

    /**
     * Reserves an image for the owner. The mocked S3 reports every key as uploaded.
     */
    private void uploadImage(String key) {
        entityManager.persist(new ImageEntity(owner, Instant.now(), key, ImageUploadStatus.RESERVED));
    }
}
