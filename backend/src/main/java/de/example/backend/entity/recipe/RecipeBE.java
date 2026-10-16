package de.example.backend.entity.recipe;

import de.example.backend.entity.*;
import de.example.backend.entity.recipe.recipestep.RecipeStepBE;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.*;

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
    private Set<RecipeIngredientBE> ingredients = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeStepBE> steps = new HashSet<>();

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

    private String text;

    @URL(regexp = "^https?://.*")
    private String sourceUrl;

    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(
            name = "recipe_images",
            joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "position")
    private List<RecipeImage> images = new ArrayList<>();

    public List<RecipeImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void addImage(RecipeImage image) {
        images.add(image);
    }

    public void removeImage(String storageKey) {
        images.removeIf(image ->
                image.getStorageKey().equals(storageKey));
    }

    public Optional<RecipeImage> getCoverImage() {
        return images.stream().findFirst();
    }

    public Set<RecipeIngredientBE> getIngredients() {
        return Collections.unmodifiableSet(ingredients);
    }

    public Set<RecipeStepBE> getSteps() {
        return Collections.unmodifiableSet(steps);
    }

    public Set<RecipeCuisineBE> getCuisines() {
        return Collections.unmodifiableSet(cuisines);
    }
}
