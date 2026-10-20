package de.example.backend.entity.recipe.recipe_step_methods;

import de.example.backend.entity.recipe.RecipeStepBE;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = "OVEN")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by Hibernate
public class OvenStepBE extends RecipeStepBE {

    @Min(30)
    @Max(300)
    private Integer temperatureC;

    public enum OvenMode {
        TOP_BOTTOM_HEAT,
        CIRCULATING_AIR
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "oven_mode")
    private OvenMode ovenMode;

    @PositiveOrZero
    private Integer durationSeconds;

    private Boolean preheat;

    public OvenStepBE(
            String description, Integer position,
            int temperatureC, OvenMode ovenMode,
            int durationSeconds, boolean preheat
    ) {
        super(description, position);
        this.temperatureC = temperatureC;
        this.ovenMode = ovenMode;
        this.durationSeconds = durationSeconds;
        this.preheat = preheat;
    }

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.OVEN;
    }

    @AssertTrue(message = "Oven step requires temperature, mode and duration")
    public boolean isComplete() {
        return temperatureC != null && ovenMode != null && durationSeconds != null;
    }
}
