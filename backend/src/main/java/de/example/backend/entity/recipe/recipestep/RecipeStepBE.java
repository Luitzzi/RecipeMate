package de.example.backend.entity.recipe.recipestep;

import de.example.backend.entity.recipe.RecipeBE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = RecipeStepBE.tableName)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "method", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class RecipeStepBE {
    public static final String tableName = "recipe_steps";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private RecipeBE recipe;

    @Column(nullable = false)
    private int position;

    @NotBlank
    @Column(nullable = false)
    private String description;

    public abstract StepMethod getStepMethod();
}
