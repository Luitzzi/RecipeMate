package de.example.backend.entity.recipe;

import de.example.backend.entity.*;
import de.example.backend.entity.ingredient.IngredientBE;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;

@Entity
@Table(name = RecipeBE.tableName)
@Getter
@Setter
@NoArgsConstructor
public class RecipeBE {
    public static final String tableName = "recipies";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<RecipeIngredientBE> ingredients = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<RecipeStepBE> steps = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = RecipeCuisineBE.tableName,
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<RecipeCuisineBE> cuisines = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = RecipePropertyBE.tableName,
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"))
    private Set<RecipePropertyBE> properties = new HashSet<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer cookTimeInMinutes;

    @Column(nullable = false)
    private Integer difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Diet diet;

    private String note;

    @URL(regexp = "^https?://.*")
    private String sourceUrl;

    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(
            name = "recipe_images",
            joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "position")
    private List<String> imageKeys = new ArrayList<>();

    public List<RecipeIngredientBE> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void addIngredient(IngredientBE ingredient, BigDecimal amount, Unit unit, int position) {
        if (containsIngredient(ingredient))
            throw new IllegalArgumentException("Ingredient " + ingredient.getId() + " is already part of the recipe");

        ingredients.add(position, new RecipeIngredientBE(this, ingredient, amount, unit));
        reindex(ingredients, RecipeIngredientBE::setPosition);
    }

    public void removeIngredient(IngredientBE ingredient) {
        boolean removed = ingredients.removeIf(recipeIngredient ->
                recipeIngredient.getIngredient().equals(ingredient));
        if (removed)
            reindex(ingredients, RecipeIngredientBE::setPosition);
    }

    private boolean containsIngredient(IngredientBE ingredient) {
        return ingredients.stream()
                .anyMatch(recipeIngredient ->
                        recipeIngredient.getIngredient().equals(ingredient));
    }

    public List<RecipeStepBE> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(RecipeStepBE step, int position) {
        step.setRecipe(this);
        steps.add(position, step);
        reindex(steps, RecipeStepBE::setPosition);
    }

    public void removeStep(RecipeStepBE step) {
        boolean removed = steps.remove(step);
        if (removed)
            reindex(steps, RecipeStepBE::setPosition);
    }

    private <T> void reindex(List<T> list, BiConsumer<T, Integer> setPosition) {
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            setPosition.accept(element, i);
        }
    }

    public Set<RecipeCuisineBE> getCuisines() {
        return Collections.unmodifiableSet(cuisines);
    }

    public List<String> getImageKeys() {
        return Collections.unmodifiableList(imageKeys);
    }

    public void addImage(String imageKey) {
        imageKeys.add(imageKey);
    }

    public void removeImage(String storageKey) {
        imageKeys.remove(storageKey);
    }

    public Optional<String> getCoverImage() {
        return imageKeys.stream()
                .findFirst();
    }
}
