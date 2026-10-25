package de.example.backend.boundary.uidto;

import de.example.backend.entity.Unit;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingListItemUiDTO {

    @Nullable
    private Long id;

    @Valid
    @Nullable
    private IngredientUiDTO ingredient;

    @Nullable
    private String name;

    @Nullable
    @PositiveOrZero
    private BigDecimal amount;

    @Nullable
    private Unit unit;

    private boolean checked;
}
