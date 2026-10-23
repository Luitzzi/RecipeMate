package de.example.backend.boundary.uidto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The stepMethod property decides which subtype is used. Names match RecipeStepEntity.StepMethod
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "stepMethod")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrepareStepUiDTO.class, name = "PREPARE"),
        @JsonSubTypes.Type(value = OvenStepUiDTO.class, name = "OVEN"),
        @JsonSubTypes.Type(value = StoveStepUiDTO.class, name = "STOVE")
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
