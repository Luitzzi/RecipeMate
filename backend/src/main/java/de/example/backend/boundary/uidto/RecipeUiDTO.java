package de.example.backend.boundary.uidto;

import de.example.backend.entity.Diet;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @PositiveOrZero
    private Integer cookTimeInMinutes;

    private Integer difficulty;

    private Diet diet;

    @Nullable
    private String note;

    @Nullable
    private String sourceUrl;

    // The order of the list defines the position
    @Valid
    @NotNull
    private List<RecipeIngredientUiDTO> ingredients = new ArrayList<>();

    // The order of the list defines the position
    @Valid
    @NotNull
    private List<RecipeStepUiDTO> steps = new ArrayList<>();

    @Valid
    @NotNull
    private List<RecipeCuisineUiDTO> cuisines = new ArrayList<>();

    @Valid
    @NotNull
    private List<RecipePropertyUiDTO> properties = new ArrayList<>();

    // The order of the list defines the position
    @NotNull
    private List<String> imageKeys = new ArrayList<>();
}
