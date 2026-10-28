package de.example.backend.dataschema;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.boundary.dto.recipe.OvenStepDTO;
import de.example.backend.boundary.dto.recipe.PrepareStepDTO;
import de.example.backend.boundary.dto.recipe.StoveStepDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeIngredientUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.OvenStepEntity.OvenMode;
import de.example.backend.entity.recipe.StoveStepEntity.HeatLevel;
import de.example.backend.entity.recipe.Unit;

import java.math.BigDecimal;

/**
 * Stateless builders for recipe DTOs used in tests.
 */
public final class RecipeTestData {

    private RecipeTestData() {
    }

    public static RecipeUiDTO createRecipeUiDTO() {
        RecipeUiDTO recipe = new RecipeUiDTO();
        recipe.setTitle("Spaghetti Bolognese");
        recipe.setDescription("Classic italian pasta");
        recipe.setCookTimeInMinutes(30);
        recipe.setDifficulty(2);
        recipe.setDiet(Diet.OMNIVORE);
        recipe.setNote("Tastes better the next day");
        return recipe;
    }

    public static RecipeIngredientUiDTO standingIngredient(IngredientEntity ingredient, BigDecimal amount, Unit unit) {
        IngredientUiDTO ingredientUiDTO = new IngredientUiDTO();
        ingredientUiDTO.setId(ingredient.getId());
        ingredientUiDTO.setName(ingredient.getName(Language.EN));
        return recipeIngredient(ingredientUiDTO, amount, unit);
    }

    public static RecipeIngredientUiDTO customIngredient(String name, BigDecimal amount, Unit unit) {
        IngredientUiDTO ingredientUiDTO = new IngredientUiDTO();
        ingredientUiDTO.setName(name);
        return recipeIngredient(ingredientUiDTO, amount, unit);
    }

    public static PrepareStepDTO prepareStep(String description) {
        PrepareStepDTO step = new PrepareStepDTO();
        step.setDescription(description);
        return step;
    }

    public static OvenStepDTO ovenStep(
            String description, int temperatureC, OvenMode ovenMode,
            int durationSeconds, boolean preheat) {
        OvenStepDTO step = new OvenStepDTO();
        step.setDescription(description);
        step.setTemperatureC(temperatureC);
        step.setOvenMode(ovenMode);
        step.setDurationSeconds(durationSeconds);
        step.setPreheat(preheat);
        return step;
    }

    public static StoveStepDTO stoveStep(String description, HeatLevel heatLevel) {
        StoveStepDTO step = new StoveStepDTO();
        step.setDescription(description);
        step.setHeatLevel(heatLevel);
        return step;
    }

    private static RecipeIngredientUiDTO recipeIngredient(IngredientUiDTO ingredient, BigDecimal amount, Unit unit) {
        RecipeIngredientUiDTO recipeIngredient = new RecipeIngredientUiDTO();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(unit);
        return recipeIngredient;
    }
}
