package de.example.backend.boundary.dto.recipe.ui;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.boundary.dto.recipe.RecipeIngredientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredientUiDTO extends RecipeIngredientDTO {

    @Valid
    @NotNull
    private IngredientUiDTO ingredient;
}
