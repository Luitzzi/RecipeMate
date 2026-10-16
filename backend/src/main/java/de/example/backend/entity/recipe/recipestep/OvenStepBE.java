package de.example.backend.entity.recipe.recipestep;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@DiscriminatorValue(value = "OVEN")
public class OvenStepBE extends RecipeStepBE {

    @Min(30)
    @Max(300)
    private Integer temperatureC;

    enum OvenMode {
        TOP_BOTTOM_HEAT,
        CIRCULATING_AIR
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "oven_mode")
    private OvenMode ovenMode;

    @PositiveOrZero
    private Integer durationSeconds;

    private Boolean preheat;

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.OVEN;
    }

    @AssertTrue(message = "Oven step requires temperature, mode and duration")
    public boolean isComplete() {
        return temperatureC != null && ovenMode != null && durationSeconds != null;
    }
}
