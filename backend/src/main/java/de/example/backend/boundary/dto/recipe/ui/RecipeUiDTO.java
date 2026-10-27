package de.example.backend.boundary.dto.recipe.ui;

import de.example.backend.boundary.dto.recipe.RecipeDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class RecipeUiDTO extends RecipeDTO {

    @Nullable
    private Long id;

    // Note: The order of the list defines the position
    @NotNull
    private List<@Valid @NotNull RecipeIngredientUiDTO> ingredients = new ArrayList<>();

    // Note: The order of the list defines the position
    @NotNull
    private List<@NotBlank String> imageKeys = new ArrayList<>();
}
