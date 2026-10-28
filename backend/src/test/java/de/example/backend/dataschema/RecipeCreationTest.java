package de.example.backend.dataschema;

import de.example.backend.boundary.dto.recipe.OvenStepDTO;
import de.example.backend.boundary.dto.recipe.PrepareStepDTO;
import de.example.backend.boundary.dto.recipe.StoveStepDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.*;
import de.example.backend.entity.recipe.OvenStepEntity.OvenMode;
import de.example.backend.entity.recipe.StoveStepEntity.HeatLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static de.example.backend.dataschema.RecipeTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RecipeCreationTest extends AbstractRecipeServiceTest {

    @Test
    void create_persistsRecipeWithCustomIngredients() {
        long ingredientCountBefore = ingredientRepository.count();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getIngredients().add(customIngredient("Saffron", new BigDecimal("0.5"), Unit.GRAM));
        recipe.getIngredients().add(customIngredient("Truffle", new BigDecimal("20"), Unit.GRAM));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        RecipeEntity loaded = loadRecipe(created);
        assertRecipeFields(loaded);
        assertThat(ingredientRepository.count()).isEqualTo(ingredientCountBefore + 2);
        assertThat(loaded.getIngredients()).hasSize(2);
        assertCustomIngredient(loaded.getIngredients().get(0), "Saffron", new BigDecimal("0.5"), Unit.GRAM);
        assertCustomIngredient(loaded.getIngredients().get(1), "Truffle", new BigDecimal("20"), Unit.GRAM);
    }

    @Test
    void create_persistsRecipeWithStandingDataIngredients() {
        long ingredientCountBefore = ingredientRepository.count();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getIngredients().add(standingIngredient(spaghetti, new BigDecimal("500"), Unit.GRAM));
        recipe.getIngredients().add(standingIngredient(ketchup, new BigDecimal("2"), Unit.TABLESPOON));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        RecipeEntity loaded = loadRecipe(created);
        assertRecipeFields(loaded);
        assertThat(ingredientRepository.count()).isEqualTo(ingredientCountBefore);
        assertThat(loaded.getIngredients())
                .extracting(recipeIngredient -> recipeIngredient.getIngredient().getId())
                .containsExactly(spaghetti.getId(), ketchup.getId());
        assertThat(loaded.getIngredients().get(0).getAmount()).isEqualByComparingTo("500");
        assertThat(loaded.getIngredients().get(0).getUnit()).isEqualTo(Unit.GRAM);
        assertThat(loaded.getIngredients().get(0).getIngredient().getCreatedBy()).isNull();
        assertThat(loaded.getIngredients().get(1).getAmount()).isEqualByComparingTo("2");
        assertThat(loaded.getIngredients().get(1).getUnit()).isEqualTo(Unit.TABLESPOON);
        assertThat(loaded.getIngredients().get(1).getIngredient().getCreatedBy()).isNull();
    }

    @Test
    void create_persistsRecipeWithMixedIngredients() {
        long ingredientCountBefore = ingredientRepository.count();
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getIngredients().add(standingIngredient(spaghetti, new BigDecimal("500"), Unit.GRAM));
        recipe.getIngredients().add(customIngredient("Basil", new BigDecimal("1"), Unit.BUNCH));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        RecipeEntity loaded = loadRecipe(created);
        assertRecipeFields(loaded);
        assertThat(ingredientRepository.count()).isEqualTo(ingredientCountBefore + 1);
        assertThat(loaded.getIngredients()).hasSize(2);
        assertThat(loaded.getIngredients().get(0).getIngredient().getId()).isEqualTo(spaghetti.getId());
        assertThat(loaded.getIngredients().get(0).getIngredient().getCreatedBy()).isNull();
        assertCustomIngredient(loaded.getIngredients().get(1), "Basil", new BigDecimal("1"), Unit.BUNCH);
    }

    @Test
    void create_keepsIngredientAndStepOrder() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getIngredients().add(standingIngredient(ketchup, new BigDecimal("2"), Unit.TABLESPOON));
        recipe.getIngredients().add(customIngredient("Basil", new BigDecimal("1"), Unit.BUNCH));
        recipe.getIngredients().add(standingIngredient(spaghetti, new BigDecimal("500"), Unit.GRAM));
        recipe.getSteps().add(stoveStep("Boil water", HeatLevel.HIGH));
        recipe.getSteps().add(prepareStep("Chop basil"));
        recipe.getSteps().add(ovenStep("Bake", 180, OvenMode.CIRCULATING_AIR, 600, true));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        RecipeEntity loaded = loadRecipe(created);
        assertThat(loaded.getIngredients())
                .extracting(recipeIngredient -> recipeIngredient.getIngredient().getName(Language.EN))
                .containsExactly("Ketchup", "Basil", "Spaghetti");
        assertThat(loaded.getIngredients())
                .extracting(RecipeIngredientEntity::getPosition)
                .containsExactly(0, 1, 2);
        assertThat(loaded.getSteps())
                .extracting(RecipeStepEntity::getDescription)
                .containsExactly("Boil water", "Chop basil", "Bake");
        assertThat(loaded.getSteps())
                .extracting(RecipeStepEntity::getPosition)
                .containsExactly(0, 1, 2);
    }

    @Test
    void create_persistsAllStepTypes() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getSteps().add(prepareStep("Chop onions"));
        recipe.getSteps().add(ovenStep("Bake", 180, OvenMode.TOP_BOTTOM_HEAT, 1800, true));
        recipe.getSteps().add(stoveStep("Fry", HeatLevel.MEDIUM));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(created.getSteps())
                .hasExactlyElementsOfTypes(PrepareStepDTO.class, OvenStepDTO.class, StoveStepDTO.class);

        List<RecipeStepEntity> steps = loadRecipe(created).getSteps();
        assertThat(steps).hasSize(3);
        assertThat(steps.get(0)).isInstanceOfSatisfying(PrepareStepEntity.class,
                step -> assertThat(step.getDescription()).isEqualTo("Chop onions"));
        assertThat(steps.get(1)).isInstanceOfSatisfying(OvenStepEntity.class, step -> {
            assertThat(step.getDescription()).isEqualTo("Bake");
            assertThat(step.getTemperatureC()).isEqualTo(180);
            assertThat(step.getOvenMode()).isEqualTo(OvenMode.TOP_BOTTOM_HEAT);
            assertThat(step.getDurationSeconds()).isEqualTo(1800);
            assertThat(step.getPreheat()).isTrue();
        });
        assertThat(steps.get(2)).isInstanceOfSatisfying(StoveStepEntity.class, step -> {
            assertThat(step.getDescription()).isEqualTo("Fry");
            assertThat(step.getHeatLevel()).isEqualTo(HeatLevel.MEDIUM);
        });
    }

    @Test
    void create_persistsCuisinesAndProperties() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setCuisines(Set.of(Cuisine.ITALIAN, Cuisine.MEDITERRANEAN));
        recipe.setProperties(Set.of(RecipeProperty.DINNER, RecipeProperty.QUICK));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(created.getCuisines()).containsExactlyInAnyOrder(Cuisine.ITALIAN, Cuisine.MEDITERRANEAN);
        assertThat(created.getProperties()).containsExactlyInAnyOrder(RecipeProperty.DINNER, RecipeProperty.QUICK);

        RecipeEntity loaded = loadRecipe(created);
        assertThat(loaded.getCuisines()).containsExactlyInAnyOrder(Cuisine.ITALIAN, Cuisine.MEDITERRANEAN);
        assertThat(loaded.getProperties()).containsExactlyInAnyOrder(RecipeProperty.DINNER, RecipeProperty.QUICK);
    }

    @Test
    void create_ignoresSourceUrl() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setSourceUrl("https://example.com/recipe");

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(created.getSourceUrl()).isNull();
        assertThat(loadRecipe(created).getSourceUrl()).isNull();
    }

    private void assertRecipeFields(RecipeEntity loaded) {
        assertThat(loaded.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(loaded.getTitle()).isEqualTo("Spaghetti Bolognese");
        assertThat(loaded.getDescription()).isEqualTo("Classic italian pasta");
        assertThat(loaded.getCookTimeInMinutes()).isEqualTo(30);
        assertThat(loaded.getDifficulty()).isEqualTo(2);
        assertThat(loaded.getDiet()).isEqualTo(Diet.OMNIVORE);
        assertThat(loaded.getNote()).isEqualTo("Tastes better the next day");
    }

    private void assertCustomIngredient(
            RecipeIngredientEntity recipeIngredient,
            String name,
            BigDecimal amount,
            Unit unit) {
        IngredientEntity ingredient = recipeIngredient.getIngredient();
        assertThat(ingredient.getId()).isNotNull();
        assertThat(ingredient.getCreatedBy().getId()).isEqualTo(owner.getId());
        for (Language language : Language.values()) {
            assertThat(ingredient.getName(language)).isEqualTo(name);
        }
        assertThat(recipeIngredient.getAmount()).isEqualByComparingTo(amount);
        assertThat(recipeIngredient.getUnit()).isEqualTo(unit);
    }
}
