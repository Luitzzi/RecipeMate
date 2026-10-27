package de.example.backend.boundary.dto.recipe;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredientAiDTO extends RecipeIngredientDTO {

    @NotBlank
    @JsonPropertyDescription("Name of the ingredient, e.g. \"onion\". Matched against the known ingredients")
    private String name;
}
