package de.example.backend.entity.recipe.recipestep;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;

@Entity
@DiscriminatorValue(value = "STOVE")
public class StoveStepBE extends RecipeStepBE {

    enum HeatLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    @Enumerated(value = EnumType.STRING)
    private HeatLevel heatLevel;

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.STOVE;
    }

    @AssertTrue(message = "Stove step requires a heat level")
    public boolean isCompleted() {
        return heatLevel != null;
    }
}
