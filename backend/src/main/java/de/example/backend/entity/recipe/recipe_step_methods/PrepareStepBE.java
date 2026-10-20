package de.example.backend.entity.recipe.recipe_step_methods;

import de.example.backend.entity.recipe.RecipeStepBE;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PREPARE")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by Hibernate
public class PrepareStepBE extends RecipeStepBE {

    public PrepareStepBE(String description, Integer position) {
        super(description, position);
    }

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.PREPARE;
    }
}
