package de.example.backend.entity.shoppinglist;

import de.example.backend.entity.ingredient.IngredientEntity;

import de.example.backend.entity.recipe.Unit;
import de.example.backend.entity.user.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

@Entity
@Table(name = ShoppingListEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
public class ShoppingListEntity {
    public static final String tableName = "shopping_list";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", unique = true)
    private UserEntity owner;

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "shoppingList",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<ShoppingListItemEntity> items = new ArrayList<>();

    public ShoppingListEntity(UserEntity owner) {
        this.owner = owner;
    }

    public List<ShoppingListItemEntity> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addIngredientItem(IngredientEntity ingredient, @Nullable BigDecimal amount, @Nullable Unit unit) {
        if (containsIngredient(ingredient))
            throw new IllegalArgumentException("Ingredient " + ingredient.getId() + " is already part of the shopping list");

        items.add(new ShoppingListItemEntity(this, ingredient, null, amount, unit));
        reindex(items, ShoppingListItemEntity::setPosition);
    }

    public void addFreeTextItem(String name, @Nullable BigDecimal amount, @Nullable Unit unit) {
        items.add(new ShoppingListItemEntity(this, null, name, amount, unit));
        reindex(items, ShoppingListItemEntity::setPosition);
    }

    public void removeItem(Long itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed)
            reindex(items, ShoppingListItemEntity::setPosition);
    }

    public void setItemChecked(Long itemId, boolean checked) {
        getItem(itemId).setChecked(checked);
    }

    private ShoppingListItemEntity getItem(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Shopping list item " + itemId + " not found"));
    }

    private boolean containsIngredient(IngredientEntity ingredient) {
        return items.stream()
                .anyMatch(item -> ingredient.equals(item.getIngredient()));
    }

    private <T> void reindex(List<T> list, BiConsumer<T, Integer> setPosition) {
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            setPosition.accept(element, i);
        }
    }
}
