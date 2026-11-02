package de.example.backend.entity.mealprep;

import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = MealPlanDayEntity.tableName, uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
public class MealPlanDayEntity {
    public static final String tableName = "meal_plan_days";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean goShopping;

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(
            name = "meal_plan_day_recipes",
            joinColumns = @JoinColumn(name = "meal_plan_day_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "position")
    @NotNull
    private List<@NotNull RecipeEntity> recipes = new ArrayList<>();

    public List<RecipeEntity> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    public void replaceRecipes(List<RecipeEntity> recipes) {
        this.recipes.clear();
        this.recipes.addAll(recipes);
    }

    public boolean isEmpty() {
        return recipes.isEmpty() && !goShopping;
    }
}
