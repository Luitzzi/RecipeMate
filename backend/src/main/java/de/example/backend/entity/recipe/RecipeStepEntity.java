package de.example.backend.entity.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = RecipeStepEntity.tableName)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "method", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by Hibernate
public abstract class RecipeStepEntity {
    public static final String tableName = "recipe_steps";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private RecipeEntity recipe;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @Setter(AccessLevel.PROTECTED) // Only RecipeBE sets the position
    @Column(nullable = false)
    private Integer position;

    protected RecipeStepEntity(String description, Integer position) {
        this.description = description;
        this.position = position;
    }

    public enum StepMethod {
        PREPARE,
        OVEN,
        STOVE
    }

    public abstract StepMethod getStepMethod();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Ensures x.equals(x) == true
        if (!(o instanceof RecipeStepEntity that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId()); // Id is null if the entity was not persisted yet. If so comparing it by id is not possible.
    }

    @Override
    public int hashCode() {
        // Id is only assigned on persist -> Constant hashcode
        return RecipeStepEntity.class.hashCode();
    }
}
