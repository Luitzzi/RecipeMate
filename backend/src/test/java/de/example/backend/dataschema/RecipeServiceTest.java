package de.example.backend.dataschema;

import de.example.backend.boundary.uidto.*;
import de.example.backend.entity.*;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity.OvenMode;
import de.example.backend.entity.recipe.recipe_step_methods.PrepareStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity.HeatLevel;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.IngredientService;
import de.example.backend.service.RecipeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DataSchemaTestUtils dataSchemaTestUtils;

    @Autowired
    private EntityManager entityManager;

    private UserEntity owner;
    // Ingredients representing the standing data
    private IngredientEntity spaghetti;
    private IngredientEntity ketchup;

    @BeforeEach
    void setUp() {
        createOwner();
        createMockIngredientStandingData();
        dataSchemaTestUtils.flushAndClear();
    }

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
    void create_keepsIngredientAndStepAndImageOrder() {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.getIngredients().add(standingIngredient(ketchup, new BigDecimal("2"), Unit.TABLESPOON));
        recipe.getIngredients().add(customIngredient("Basil", new BigDecimal("1"), Unit.BUNCH));
        recipe.getIngredients().add(standingIngredient(spaghetti, new BigDecimal("500"), Unit.GRAM));
        recipe.getSteps().add(stoveStep("Boil water", HeatLevel.HIGH));
        recipe.getSteps().add(prepareStep("Chop basil"));
        recipe.getSteps().add(ovenStep("Bake", 180, OvenMode.CIRCULATING_AIR, 600, true));
        recipe.setImageKeys(List.of("image-a", "image-b", "image-c"));

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
        assertThat(loaded.getImageKeys()).containsExactly("image-a", "image-b", "image-c");
        assertThat(loaded.getCoverImage()).contains("image-a");
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
                .hasExactlyElementsOfTypes(PrepareStepUiDTO.class, OvenStepUiDTO.class, StoveStepUiDTO.class);

        List<RecipeStepEntity> steps = loadRecipe(created).getSteps();
        assertThat(steps).hasSize(3);
        assertThat(steps.get(0)).isInstanceOfSatisfying(PrepareStepEntity.class, step ->
                assertThat(step.getDescription()).isEqualTo("Chop onions"));
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
        recipe.setCuisines(List.of(Cuisine.ITALIAN, Cuisine.MEDITERRANEAN));
        recipe.setProperties(List.of(RecipeProperty.DINNER, RecipeProperty.QUICK));

        RecipeUiDTO created = recipeService.create(recipe, owner);
        dataSchemaTestUtils.flushAndClear();

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

    private RecipeEntity loadRecipe(RecipeUiDTO created) {
        assertThat(created.getId()).isNotNull();
        return recipeRepository.findById(created.getId()).orElseThrow();
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
            Unit unit
    ) {
        IngredientEntity ingredient = recipeIngredient.getIngredient();
        assertThat(ingredient.getId()).isNotNull();
        assertThat(ingredient.getCreatedBy().getId()).isEqualTo(owner.getId());
        for (Language language : Language.values()) {
            assertThat(ingredient.getName(language)).isEqualTo(name);
        }
        assertThat(recipeIngredient.getAmount()).isEqualByComparingTo(amount);
        assertThat(recipeIngredient.getUnit()).isEqualTo(unit);
    }

    private RecipeUiDTO createRecipeUiDTO() {
        RecipeUiDTO recipe = new RecipeUiDTO();
        recipe.setTitle("Spaghetti Bolognese");
        recipe.setDescription("Classic italian pasta");
        recipe.setCookTimeInMinutes(30);
        recipe.setDifficulty(2);
        recipe.setDiet(Diet.OMNIVORE);
        recipe.setNote("Tastes better the next day");
        return recipe;
    }

    private RecipeIngredientUiDTO standingIngredient(IngredientEntity ingredient, BigDecimal amount, Unit unit) {
        IngredientUiDTO ingredientUiDTO = new IngredientUiDTO();
        ingredientUiDTO.setId(ingredient.getId());
        ingredientUiDTO.setName(ingredient.getName(Language.EN));
        return recipeIngredient(ingredientUiDTO, amount, unit);
    }

    private RecipeIngredientUiDTO customIngredient(String name, BigDecimal amount, Unit unit) {
        IngredientUiDTO ingredientUiDTO = new IngredientUiDTO();
        ingredientUiDTO.setName(name);
        return recipeIngredient(ingredientUiDTO, amount, unit);
    }

    private RecipeIngredientUiDTO recipeIngredient(IngredientUiDTO ingredient, BigDecimal amount, Unit unit) {
        RecipeIngredientUiDTO recipeIngredient = new RecipeIngredientUiDTO();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(unit);
        return recipeIngredient;
    }

    private PrepareStepUiDTO prepareStep(String description) {
        PrepareStepUiDTO step = new PrepareStepUiDTO();
        step.setDescription(description);
        return step;
    }

    private OvenStepUiDTO ovenStep(
            String description, int temperatureC, OvenMode ovenMode,
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

    private StoveStepUiDTO stoveStep(String description, HeatLevel heatLevel) {
        StoveStepUiDTO step = new StoveStepUiDTO();
        step.setDescription(description);
        step.setHeatLevel(heatLevel);
        return step;
    }

    private void createOwner() {
        owner = new UserEntity();
        entityManager.persist(owner);
    }

    private void createMockIngredientStandingData() {
        spaghetti = new IngredientEntity();
        spaghetti.setType(IngredientType.PASTA);
        spaghetti.setCustomName("Spaghetti");

        ketchup = new IngredientEntity();
        ketchup.setType(IngredientType.OTHER);
        ketchup.setCustomName("Ketchup");

        ingredientService.createAll(List.of(spaghetti, ketchup));
    }
}
