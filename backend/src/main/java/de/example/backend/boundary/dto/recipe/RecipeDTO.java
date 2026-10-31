package de.example.backend.boundary.dto.recipe;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import de.example.backend.entity.recipe.Cuisine;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fields of a recipe shared by all recipients (UI, AI).
 * Subclasses add recipient specific fields, e.g. ids or the ingredient
 * representation.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class RecipeDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Integer cookTimeInMinutes;

    @NotNull
    @Min(RecipeEntity.DIFFICULTY_MIN)
    @Max(RecipeEntity.DIFFICULTY_MAX)
    @JsonPropertyDescription("1 = very easy, 4 = very hard")
    private Integer difficulty;

    @NotNull
    private Diet diet;

    @Nullable
    @JsonPropertyDescription("A note about the recipe, e.g. special instructions or tips. Might be empty.")
    private String note;

    @Nullable
    @URL(regexp = "^https?://.*")
    @JsonPropertyDescription("Only set if the recipe is based on a website. Must start with http:// or https://")
    private String sourceUrl;

    // Note: The order of the list defines the position
    @NotNull
    private List<@Valid @NotNull RecipeStepDTO> steps = new ArrayList<>();

    @NotNull
    private Set<@NotNull Cuisine> cuisines = new HashSet<>();

    @NotNull
    private Set<@NotNull RecipeProperty> properties = new HashSet<>();
}
