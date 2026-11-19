package de.example.backend.service;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.feed.FeedSectionType;
import de.example.backend.boundary.dto.feed.FeedSectionUiDTO;
import de.example.backend.boundary.dto.feed.RecipeSummaryUiDTO;
import de.example.backend.entity.recipe.RecipeProperty;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.querydsl.RecipeQueries;
import de.example.backend.repository.querydsl.RecipeSummaryProjection;
import de.example.backend.service.exception.feed.InvalidFeedSectionException;
import de.example.backend.service.image_storage.ImageStorageService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* Builds the sections (rows) of the feed page */
@Service
@RequiredArgsConstructor
public class FeedService {

    // TODO OSA: Add a FAVORITES section as soon as favorites exist in the data model
    /** Sections of the feed, top to bottom. */
    private static final List<FeedSection> SECTIONS = List.of(
            FeedSection.recent(),
            FeedSection.property(RecipeProperty.BREAKFAST),
            FeedSection.property(RecipeProperty.MAIN_COURSE),
            FeedSection.property(RecipeProperty.QUICK),
            FeedSection.property(RecipeProperty.DESSERT));

    private final RecipeQueries recipeQueries;
    private final ImageStorageService imageStorageService;

    /**
     * @param size Number of recipes in the first page of each section
     * @return The sections in feed order with their first page. Sections without recipes are omitted.
     */
    public List<FeedSectionUiDTO> getSections(UserEntity owner, int size) {
        Pageable firstPage = PageRequest.of(0, size);
        return SECTIONS.stream()
                .map(section -> new FeedSectionUiDTO(
                        section.type(),
                        section.property(),
                        getPage(section, owner, firstPage)))
                .filter(section -> section.getRecipes().getTotalElements() > 0)
                .toList();
    }

    /**
     * Loads a further page of one section.
     *
     * @param property Required for {@link FeedSectionType#PROPERTY}, must be null otherwise
     * @throws InvalidFeedSectionException If the property does not match the type
     */
    public PageUiDTO<RecipeSummaryUiDTO> getSectionPage(FeedSectionType type, @Nullable RecipeProperty property,
            UserEntity owner, Pageable pageable) {
        return getPage(FeedSection.of(type, property), owner, pageable);
    }

    private PageUiDTO<RecipeSummaryUiDTO> getPage(FeedSection section, UserEntity owner, Pageable pageable) {
        Page<RecipeSummaryProjection> recipes = recipeQueries.findSummaryPageOfOwnerWithProperties(
                owner, section.properties(), pageable);

        List<String> coverImageKeys = recipes.stream()
                .map(RecipeSummaryProjection::coverImageKey)
                .filter(Objects::nonNull)
                .toList();

        Map<String, String> coverImageUrls = imageStorageService.createDownloadUrls(coverImageKeys, owner);

        return PageUiDTO.from(recipes.map(recipe -> toSummaryUiDTO(recipe, coverImageUrls)));
    }

    private RecipeSummaryUiDTO toSummaryUiDTO(RecipeSummaryProjection recipe, Map<String, String> coverImageUrls) {
        String coverImageUrl = recipe.coverImageKey() != null ? coverImageUrls.get(recipe.coverImageKey()) : null;

        return new RecipeSummaryUiDTO(
                recipe.id(),
                recipe.title(),
                recipe.cookTimeInMinutes(),
                recipe.difficulty(),
                recipe.diet(),
                coverImageUrl);
    }

    private record FeedSection(FeedSectionType type, @Nullable RecipeProperty property) {

        static FeedSection recent() {
            return new FeedSection(FeedSectionType.RECENT, null);
        }

        static FeedSection property(RecipeProperty property) {
            return new FeedSection(FeedSectionType.PROPERTY, property);
        }

        static FeedSection of(FeedSectionType type, @Nullable RecipeProperty property) {
            boolean needsProperty = type == FeedSectionType.PROPERTY;

            if (needsProperty != (property != null)) {
                throw new InvalidFeedSectionException(needsProperty
                        ? "Section type %s requires a property".formatted(type)
                        : "Section type %s must not have a property".formatted(type));
            }

            return new FeedSection(type, property);
        }

        Set<RecipeProperty> properties() {
            return property != null ? Set.of(property) : Set.of();
        }
    }
}
