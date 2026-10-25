package de.example.backend.boundary.uidto;

import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity.OvenMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class OvenStepUiDTO extends RecipeStepUiDTO {

    @Min(30)
    @Max(300)
    private Integer temperatureC;

    private OvenMode ovenMode;

    @PositiveOrZero
    private Integer durationSeconds;

    private boolean preheat;
}
