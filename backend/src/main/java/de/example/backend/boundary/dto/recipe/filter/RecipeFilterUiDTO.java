package de.example.backend.boundary.dto.recipe.filter;

import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.Cuisine;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Search criteria for recipes. A criterion that is null or empty is not applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class RecipeFilterUiDTO {

    @Nullable
    private String title;

    @Nullable
    private Language language;

    @NotNull
    private List<@NotNull Long> ingredientIds = new ArrayList<>();

    @Nullable
    private Diet diet;

    @Nullable
    @PositiveOrZero
    private Integer maxCookTimeInMinutes;

    @Nullable
    @Min(RecipeEntity.DIFFICULTY_MIN)
    @Max(RecipeEntity.DIFFICULTY_MAX)
    private Integer maxDifficulty;

    @NotNull
    private Set<@NotNull Cuisine> cuisines = new HashSet<>();

    @NotNull
    private Set<@NotNull RecipeProperty> properties = new HashSet<>();

    @Nullable
    private String folderName;

}
