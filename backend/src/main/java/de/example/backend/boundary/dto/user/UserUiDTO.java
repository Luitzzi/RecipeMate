package de.example.backend.boundary.dto.user;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.entity.recipe.Cuisine;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.user.Allergen;
import de.example.backend.entity.user.CookingSkill;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserUiDTO {

    @Nullable
    private UUID id;

    @NotNull
    private Diet diet;

    @Nullable
    private CookingSkill cookingSkill;

    @NotNull
    private List<Cuisine> preferredCuisines = new ArrayList<>();

    @NotNull
    private List<Allergen> allergens = new ArrayList<>();


    @Valid
    @NotNull
    private List<IngredientUiDTO> dislikedIngredients = new ArrayList<>();
}
