package de.example.backend.boundary.seed;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.boundary.uidto.OvenStepUiDTO;
import de.example.backend.boundary.uidto.PrepareStepUiDTO;
import de.example.backend.boundary.uidto.RecipeIngredientUiDTO;
import de.example.backend.boundary.uidto.RecipeStepUiDTO;
import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.boundary.uidto.StoveStepUiDTO;
import de.example.backend.entity.Cuisine;
import de.example.backend.entity.Diet;
import de.example.backend.entity.RecipeProperty;
import de.example.backend.entity.Unit;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import de.example.backend.service.IngredientService;
import de.example.backend.service.RecipeService;
import de.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Create a test user and a recipe for him if he does not exist yet.
 * Create the ingredient standing data if no data is in the ingredients table yet.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    private static final String INGREDIENT_CSV = "db/ingredients.csv";

    private final UserService userService;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Seeding the database...");

        log.info("Try creating the test user...");
        userService.createTestUserIfMissing();

        log.info("Try creating the ingredients standing data...");
        int createdIngredients = ingredientService.createStandingDataIfMissing(new ClassPathResource(INGREDIENT_CSV));
        if (createdIngredients > 0)
            log.info("Seeded {} standing data ingredients", createdIngredients);

        log.info("Try creating the test recipe...");
        if (!recipeService.hasRecipes(UserService.TEST_USER)) {
            RecipeUiDTO createdRecipe = recipeService.create(testRecipe(), UserService.TEST_USER);
            log.info("Seeded mock recipe {} for user {}", createdRecipe.getId(), UserService.TEST_USER.getId());
        } else {
            log.info("The test user already has a recipe. Skip creating the test recipe.");
        }
    }

    private RecipeUiDTO testRecipe() {
        RecipeUiDTO recipe = new RecipeUiDTO();
        recipe.setTitle("Rosemary Chicken with Roast Potatoes");
        recipe.setDescription("Seared chicken breast and crispy rosemary potatoes from the oven.");
        recipe.setCookTimeInMinutes(55);
        recipe.setDifficulty(2);
        recipe.setDiet(Diet.OMNIVORE);
        recipe.setNote("Mock recipe created by the database seeder.");

        recipe.getIngredients().add(ingredient("Potato", new BigDecimal("800"), Unit.GRAM));
        recipe.getIngredients().add(ingredient("Chicken Breast", new BigDecimal("2"), Unit.PIECE));
        recipe.getIngredients().add(ingredient("Garlic", new BigDecimal("3"), Unit.CLOVE));
        recipe.getIngredients().add(ingredient("Rosemary", new BigDecimal("2"), Unit.SPRING));
        recipe.getIngredients().add(ingredient("Olive Oil", new BigDecimal("3"), Unit.TABLESPOON));
        recipe.getIngredients().add(ingredient("Salt", null, Unit.TO_TASTE));
        recipe.getIngredients().add(ingredient("Black Pepper", null, Unit.TO_TASTE));

        recipe.getSteps().add(prepareStep(
                "Cut the potatoes into wedges, press the garlic and pluck the rosemary needles."));
        recipe.getSteps().add(ovenStep(
                "Toss the potatoes with olive oil, rosemary, salt and pepper and roast them until golden.",
                200, OvenStepEntity.OvenMode.CIRCULATING_AIR, 35 * 60, true));
        recipe.getSteps().add(stoveStep(
                "Season the chicken and sear it in olive oil with the garlic for 6 minutes per side.",
                StoveStepEntity.HeatLevel.MEDIUM));
        recipe.getSteps().add(prepareStep(
                "Slice the chicken and serve it on top of the potatoes."));

        recipe.getCuisines().add(Cuisine.MEDITERRANEAN);
        recipe.getProperties().add(RecipeProperty.DINNER);
        recipe.getProperties().add(RecipeProperty.MAIN_COURSE);
        recipe.getProperties().add(RecipeProperty.HIGH_PROTEIN);
        return recipe;
    }

    private RecipeIngredientUiDTO ingredient(String englishName, BigDecimal amount, Unit unit) {
        IngredientUiDTO ingredient = new IngredientUiDTO();
        ingredient.setId(ingredientService.getStandingDataByEnglishName(englishName).getId());

        RecipeIngredientUiDTO recipeIngredient = new RecipeIngredientUiDTO();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(unit);
        return recipeIngredient;
    }

    private RecipeStepUiDTO prepareStep(String description) {
        PrepareStepUiDTO step = new PrepareStepUiDTO();
        step.setDescription(description);
        return step;
    }

    private RecipeStepUiDTO ovenStep(
            String description, int temperatureC, OvenStepEntity.OvenMode ovenMode,
            int durationSeconds, boolean preheat
    ) {
        OvenStepUiDTO step = new OvenStepUiDTO();
        step.setDescription(description);
        step.setTemperatureC(temperatureC);
        step.setOvenMode(ovenMode);
        step.setDurationSeconds(durationSeconds);
        step.setPreheat(preheat);
        return step;
    }

    private RecipeStepUiDTO stoveStep(String description, StoveStepEntity.HeatLevel heatLevel) {
        StoveStepUiDTO step = new StoveStepUiDTO();
        step.setDescription(description);
        step.setHeatLevel(heatLevel);
        return step;
    }
}
