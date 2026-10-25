package de.example.backend.boundary.uidto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.PrepareStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A recipe step can be of different types e.g. PREPARE, OVEN etc.
 * Each type has different additional fields.
 * Jackson converts the Java class based on its class type to a json object
 * with the discriminating field stepMethod which gives the step type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = RecipeStepEntity.descriminatorColumnName)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrepareStepUiDTO.class, name = PrepareStepEntity.stepMethodName),
        @JsonSubTypes.Type(value = OvenStepUiDTO.class, name = OvenStepEntity.stepMethodName),
        @JsonSubTypes.Type(value = StoveStepUiDTO.class, name = StoveStepEntity.stepMethodName)
})
@Getter
@Setter
@NoArgsConstructor
public abstract sealed class RecipeStepUiDTO permits PrepareStepUiDTO, OvenStepUiDTO, StoveStepUiDTO {

    @Nullable
    private Long id;

    @NotBlank
    private String description;
}
