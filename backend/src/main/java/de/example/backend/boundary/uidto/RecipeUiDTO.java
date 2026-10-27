package de.example.backend.boundary.uidto;

import de.example.backend.entity.Cuisine;
import de.example.backend.entity.Diet;
import de.example.backend.entity.RecipeProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Getter
@Setter
@NoArgsConstructor
public class RecipeUiDTO {

    @Nullable
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Integer cookTimeInMinutes;

    @NotNull
    @Min(1)
    @Max(5)
    @JsonPropertyDescription("1 = very easy, 5 = very hard")
    private Integer difficulty;

    @NotNull
    private Diet diet;

    @NotNull
    private String note;

    @Nullable
    @URL(regexp = "^https?://.*")
    @JsonPropertyDescription("Only set if the recipe is based on a website. Must start with http:// or https://")
    private String sourceUrl;

    // Note: The order of the list defines the position
    @NotNull
    private List<@Valid @NotNull RecipeIngredientUiDTO> ingredients = new ArrayList<>();

    // Note: The order of the list defines the position
    @NotNull
    private List<@Valid @NotNull RecipeStepUiDTO> steps = new ArrayList<>();

    @NotNull
    private Set<@NotNull Cuisine> cuisines = new HashSet<>();

    @NotNull
    private Set<@NotNull RecipeProperty> properties = new HashSet<>();

    // Note: The order of the list defines the position
    @NotNull
    private List<@NotBlank String> imageKeys = new ArrayList<>();
}
