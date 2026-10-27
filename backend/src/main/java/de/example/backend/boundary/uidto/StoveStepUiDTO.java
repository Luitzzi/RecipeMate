package de.example.backend.boundary.uidto;

import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity.HeatLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class StoveStepUiDTO extends RecipeStepUiDTO {

    @NotNull
    private HeatLevel heatLevel;

}
