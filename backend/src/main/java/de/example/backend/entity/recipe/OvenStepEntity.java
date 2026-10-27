package de.example.backend.entity.recipe;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = OvenStepEntity.stepMethodName)
@Getter
@Setter
@NoArgsConstructor
public class OvenStepEntity extends RecipeStepEntity {
    public static final String stepMethodName = "OVEN";

    @NotNull
    @Min(30)
    @Max(300)
    private Integer temperatureC;

    public enum OvenMode {
        TOP_BOTTOM_HEAT,
        CIRCULATING_AIR
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "oven_mode")
    private OvenMode ovenMode;

    // Null if the step has no fixed time and ends when the food is visibly done
    @Nullable
    @Positive
    private Integer durationSeconds;

    @NotNull
    private Boolean preheat;

    public OvenStepEntity(
            String description, Integer position,
            int temperatureC, OvenMode ovenMode,
            @Nullable Integer durationSeconds, boolean preheat) {
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
}
