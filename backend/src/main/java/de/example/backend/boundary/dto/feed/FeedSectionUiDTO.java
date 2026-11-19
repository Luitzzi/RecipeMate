package de.example.backend.boundary.dto.feed;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.entity.recipe.RecipeProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedSectionUiDTO {

    private final FeedSectionType type;

    @Nullable
    private final RecipeProperty property;

    private final PageUiDTO<RecipeSummaryUiDTO> recipes;
}
