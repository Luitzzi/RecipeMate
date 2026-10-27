package de.example.backend.boundary.dto.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.OvenStepEntity;
import de.example.backend.entity.recipe.PrepareStepEntity;
import de.example.backend.entity.recipe.StoveStepEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A recipe step can be of different types e.g. PREPARE, OVEN etc.
 * Each type has different additional fields.
 * Jackson converts the Java class based on its class type to a json object
 * with the discriminating field stepMethod which gives the step type.
 * Shared by all recipients (UI, AI). Steps have no id, they are always replaced
 * together with their recipe.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = RecipeStepEntity.descriminatorColumnName)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrepareStepDTO.class, name = PrepareStepEntity.stepMethodName),
        @JsonSubTypes.Type(value = OvenStepDTO.class, name = OvenStepEntity.stepMethodName),
        @JsonSubTypes.Type(value = StoveStepDTO.class, name = StoveStepEntity.stepMethodName)
})
@Getter
@Setter
@NoArgsConstructor
public abstract sealed class RecipeStepDTO permits PrepareStepDTO, OvenStepDTO, StoveStepDTO {

    @NotBlank
    private String description;
}
