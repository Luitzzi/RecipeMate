package de.example.backend.boundary.dto.recipe.ai;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import de.example.backend.boundary.dto.recipe.RecipeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeAiDTO extends RecipeDTO {

    @NotNull
    @JsonPropertyDescription("The order of the list defines the position")
    private List<@Valid @NotNull RecipeIngredientAiDTO> ingredients = new ArrayList<>();

    // Ingredients are referenced by name, so duplicates can not be detected via the
    // ingredient id
    @JsonIgnore
    @AssertTrue(message = "Each ingredient may only appear once, merge the amounts instead")
    public boolean isIngredientsUnique() {
        if (ingredients == null)
            return true;

        long distinctNames = ingredients.stream()
                .map(ingredient -> ingredient == null || ingredient.getName() == null
                        ? null
                        : ingredient.getName().trim().toLowerCase())
                .distinct()
                .count();
        return distinctNames == ingredients.size();
    }
}
