package de.example.backend.boundary.dto.recipe.validation;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeValidationAiDTO {

    @NotNull
    @JsonPropertyDescription("Every defect found. Empty if the recipe is fine")
    private List<@NotNull String> findings = new ArrayList<>();
}
