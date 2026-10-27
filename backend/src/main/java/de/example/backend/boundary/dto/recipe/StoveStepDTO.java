package de.example.backend.boundary.dto.recipe;

import de.example.backend.entity.recipe.StoveStepEntity.HeatLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class StoveStepDTO extends RecipeStepDTO {

    @NotNull
    private HeatLevel heatLevel;

}
