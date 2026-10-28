package de.example.backend.entity.user;

import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.Cuisine;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.RecipeImageEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = UserEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    public static final String tableName = "users";

    @Id
    @GeneratedValue
    private UUID id;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "createdBy")
    Set<IngredientEntity> ingredients = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "owner")
    Set<RecipeEntity> recipes = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "createdBy")
    Set<RecipeImageEntity> images = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Diet diet = Diet.OMNIVORE;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CookingSkill cookingSkill;

    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(
            name = "user_preferred_cuisines",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "cuisine", nullable = false)
    private Set<Cuisine> preferredCuisines = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(
            name = "user_allergens",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen", nullable = false)
    private Set<Allergen> allergens = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = "user_disliked_ingredients",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<IngredientEntity> dislikedIngredients = new HashSet<>();

    public UserEntity(UUID id) {
        this.id = id;
    }

    public Set<Cuisine> getPreferredCuisines() {
        return Collections.unmodifiableSet(preferredCuisines);
    }

    public void replacePreferredCuisines(Collection<Cuisine> preferredCuisines) {
        this.preferredCuisines.clear();
        this.preferredCuisines.addAll(preferredCuisines);
    }

    public Set<Allergen> getAllergens() {
        return Collections.unmodifiableSet(allergens);
    }

    public void replaceAllergens(Collection<Allergen> allergens) {
        this.allergens.clear();
        this.allergens.addAll(allergens);
    }

    public Set<IngredientEntity> getDislikedIngredients() {
        return Collections.unmodifiableSet(dislikedIngredients);
    }

    public void replaceDislikedIngredients(Collection<IngredientEntity> dislikedIngredients) {
        this.dislikedIngredients.clear();
        this.dislikedIngredients.addAll(dislikedIngredients);
    }
}
