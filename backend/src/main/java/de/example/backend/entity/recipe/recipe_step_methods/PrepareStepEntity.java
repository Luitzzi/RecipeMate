package de.example.backend.entity.recipe.recipe_step_methods;

import de.example.backend.entity.recipe.RecipeStepEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue(PrepareStepEntity.stepMethodName)
@NoArgsConstructor
public class PrepareStepEntity extends RecipeStepEntity {
    public static final String stepMethodName = "PREPARE";

    public PrepareStepEntity(String description, Integer position) {
        super(description, position);
    }

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.PREPARE;
    }
}
