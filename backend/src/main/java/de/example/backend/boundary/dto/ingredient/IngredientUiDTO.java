package de.example.backend.boundary.dto.ingredient;

import de.example.backend.entity.ingredient.IngredientType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngredientUiDTO {

    @Nullable
    private Long id;

    @NotBlank
    private String name;

    @Nullable
    private IngredientType type;
}
