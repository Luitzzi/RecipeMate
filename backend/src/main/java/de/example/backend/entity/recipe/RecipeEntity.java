package de.example.backend.entity.recipe;

import de.example.backend.entity.*;
import de.example.backend.entity.ingredient.IngredientEntity;

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
@Table(name = RecipeEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
public class RecipeEntity {
    public static final String tableName = "recipies";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<RecipeIngredientEntity> ingredients = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<RecipeStepEntity> steps = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = RecipeCuisineEntity.tableName,
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<RecipeCuisineEntity> cuisines = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = RecipePropertyEntity.tableName,
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"))
    private Set<RecipePropertyEntity> properties = new HashSet<>();

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

    public List<RecipeIngredientEntity> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void addIngredient(IngredientEntity ingredient, BigDecimal amount, Unit unit, int position) {
        if (containsIngredient(ingredient))
            throw new IllegalArgumentException("Ingredient " + ingredient.getId() + " is already part of the recipe");

        ingredients.add(position, new RecipeIngredientEntity(this, ingredient, amount, unit));
        reindex(ingredients, RecipeIngredientEntity::setPosition);
    }

    public void removeIngredient(IngredientEntity ingredient) {
        boolean removed = ingredients.removeIf(recipeIngredient ->
                recipeIngredient.getIngredient().equals(ingredient));
        if (removed)
            reindex(ingredients, RecipeIngredientEntity::setPosition);
    }

    private boolean containsIngredient(IngredientEntity ingredient) {
        return ingredients.stream()
                .anyMatch(recipeIngredient ->
                        recipeIngredient.getIngredient().equals(ingredient));
    }

    public List<RecipeStepEntity> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(RecipeStepEntity step, int position) {
        step.setRecipe(this);
        steps.add(position, step);
        reindex(steps, RecipeStepEntity::setPosition);
    }

    public void removeStep(RecipeStepEntity step) {
        boolean removed = steps.remove(step);
        if (removed)
            reindex(steps, RecipeStepEntity::setPosition);
    }

    private <T> void reindex(List<T> list, BiConsumer<T, Integer> setPosition) {
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            setPosition.accept(element, i);
        }
    }

    public Set<RecipeCuisineEntity> getCuisines() {
        return Collections.unmodifiableSet(cuisines);
    }

    public void addCuisineTags(List<RecipeCuisineEntity> cuisineTags) {
        cuisines.addAll(cuisineTags);
    }

    public Set<RecipePropertyEntity> getProperties() {
        return Collections.unmodifiableSet(properties);
    }

    public void addPropertyTags(List<RecipePropertyEntity> propertyTags) {
        properties.addAll(propertyTags);
    }

    public List<String> getImageKeys() {
        return Collections.unmodifiableList(imageKeys);
    }

    public void addImageKeys(List<String> imageKeys) {
        this.imageKeys.addAll(imageKeys);
    }

    public void removeImage(String storageKey) {
        imageKeys.remove(storageKey);
    }

    public Optional<String> getCoverImage() {
        return imageKeys.stream()
                .findFirst();
    }
}
