package de.example.backend.entity.recipe.recipestep;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PREPARE")
public class PrepareStepBE extends RecipeStepBE {

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.PREPARE;
    }
}
