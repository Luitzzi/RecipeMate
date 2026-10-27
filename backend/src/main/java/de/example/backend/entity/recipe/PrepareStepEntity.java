package de.example.backend.entity.recipe;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
