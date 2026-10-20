package de.example.backend.entity.recipe;

import de.example.backend.entity.ingredient.IngredientBE;
import de.example.backend.entity.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = RecipeIngredientBE.tableName)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by Hibernate
public class RecipeIngredientBE {
    public static final String tableName = "recipe_ingredient";

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecipeIngredientId implements Serializable {
        private Long recipeId;
        private Long ingredientId;
    }

    @Setter(AccessLevel.NONE)
    @EmbeddedId
    private RecipeIngredientId id = new RecipeIngredientId();

    @Setter(AccessLevel.NONE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @MapsId("recipeId")
    private RecipeBE recipe;

    @Setter(AccessLevel.NONE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    @MapsId("ingredientId")
    private IngredientBE ingredient;

    @PositiveOrZero
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Setter(AccessLevel.PACKAGE) // Only RecipeBE sets the position
    @Column(nullable = false)
    private Integer position;

    // Only RecipeBE creates its ingredients, so recipe and position stay consistent
    RecipeIngredientBE(RecipeBE recipe, IngredientBE ingredient, BigDecimal amount, Unit unit) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }
}
