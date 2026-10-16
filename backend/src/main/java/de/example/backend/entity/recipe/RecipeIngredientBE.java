package de.example.backend.entity.recipe;

import de.example.backend.entity.IngredientBE;
import de.example.backend.entity.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Locale;

@Entity
@Table(name = RecipeIngredientBE.tableName)
@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredientBE {
    public static final String tableName = "recipe_ingredient";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RecipeBE recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private IngredientBE ingredient;

    // Used when the user adds a new ingredient
    // that does not exist in the ingredient standing data
    @NotBlank
    private String customName;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private int position;

    @AssertTrue(message = "Exactly one of ingredient or customName must be set")
    private boolean isExactlyOneSource() {
        boolean isStandingDataIngredient = ingredient != null && customName == null;
        boolean isCreatedIngredient = ingredient == null && customName != null;
        return isStandingDataIngredient || isCreatedIngredient;
    }

    public String getIngredientName(Locale locale) {
        return ingredient != null ? ingredient.getName(locale) : customName;
    }
}
