package de.example.backend.boundary.uidto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity.OvenMode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class OvenStepUiDTO extends RecipeStepUiDTO {

    @NotNull
    @Min(30)
    @Max(300)
    private Integer temperatureC;

    @NotNull
    private OvenMode ovenMode;

    @Nullable
    @Positive
    @JsonPropertyDescription("Baking time in seconds, must be positive. Set to null if the step has no fixed time and ends when the food is visibly done (e.g. 'until golden brown').")
    private Integer durationSeconds;

    @NotNull
    private boolean preheat;
}
