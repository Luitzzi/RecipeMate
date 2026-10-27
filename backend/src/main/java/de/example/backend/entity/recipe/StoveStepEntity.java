package de.example.backend.entity.recipe;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = StoveStepEntity.stepMethodName)
@Getter
@Setter
@NoArgsConstructor
public class StoveStepEntity extends RecipeStepEntity {
    public static final String stepMethodName = "STOVE";

    public enum HeatLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private HeatLevel heatLevel;

    public StoveStepEntity(String description, HeatLevel heatLevel) {
        super();
        this.heatLevel = heatLevel;
    }

    public StoveStepEntity(
            String description, Integer position,
            HeatLevel heatLevel
    ) {
        super(description, position);
        this.heatLevel = heatLevel;
    }

    @Override
    public StepMethod getStepMethod() {
        return StepMethod.STOVE;
    }
}
