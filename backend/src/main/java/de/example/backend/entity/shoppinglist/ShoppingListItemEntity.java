package de.example.backend.entity.shoppinglist;

import de.example.backend.entity.ingredient.IngredientEntity;

import de.example.backend.entity.recipe.Unit;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = ShoppingListItemEntity.tableName)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by Hibernate
public class ShoppingListItemEntity {
    public static final String tableName = "shopping_list_item";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id")
    private ShoppingListEntity shoppingList;

    // Either ingredient (standing data / custom ingredient) or freeTextName is set, never both
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private IngredientEntity ingredient;

    @Setter(AccessLevel.NONE)
    private String freeTextName;

    @PositiveOrZero
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    private boolean checked = false;

    @Setter(AccessLevel.PACKAGE) // Only ShoppingListEntity sets the position
    @Column(nullable = false)
    private Integer position;

    // Only ShoppingListEntity creates its items, so shoppingList and position stay consistent
    ShoppingListItemEntity(
            ShoppingListEntity shoppingList,
            @Nullable IngredientEntity ingredient,
            @Nullable String freeTextName,
            @Nullable BigDecimal amount,
            @Nullable Unit unit
    ) {
        this.shoppingList = shoppingList;
        this.ingredient = ingredient;
        this.freeTextName = freeTextName;
        this.amount = amount;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingListItemEntity that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return ShoppingListItemEntity.class.hashCode();
    }
}
