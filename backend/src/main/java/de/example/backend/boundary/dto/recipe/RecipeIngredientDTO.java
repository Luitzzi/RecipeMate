package de.example.backend.boundary.dto.recipe;

import de.example.backend.entity.recipe.Unit;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Fields of a recipe ingredient shared by all recipients (UI, AI).
 * Subclasses add the recipient specific reference to the ingredient.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class RecipeIngredientDTO {

    @Nullable
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private Unit unit;
}
