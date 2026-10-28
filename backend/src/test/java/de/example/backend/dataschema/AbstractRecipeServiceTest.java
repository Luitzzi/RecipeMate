package de.example.backend.dataschema;

import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.ingredient.IngredientType;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.IngredientService;
import de.example.backend.service.RecipeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared setup for all RecipeService tests: owner, ingredient standing data and the mocked R2 client.
 */
@SpringBootTest
@Transactional
abstract class AbstractRecipeServiceTest {

    @Autowired
    protected RecipeService recipeService;

    @Autowired
    protected IngredientService ingredientService;

    @Autowired
    protected RecipeRepository recipeRepository;

    @Autowired
    protected IngredientRepository ingredientRepository;

    @Autowired
    protected DataSchemaTestUtils dataSchemaTestUtils;

    @Autowired
    protected EntityManager entityManager;

    // Declared in the base class so all subclasses share one cached Spring context
    @MockitoBean
    protected S3Client s3Client;

    protected UserEntity owner;
    // Ingredients representing the standing data
    protected IngredientEntity spaghetti;
    protected IngredientEntity ketchup;

    @BeforeEach
    void setUpRecipeData() {
        createOwner();
        createMockIngredientStandingData();
        dataSchemaTestUtils.flushAndClear();
    }

    protected RecipeEntity loadRecipe(RecipeUiDTO created) {
        assertThat(created.getId()).isNotNull();
        return recipeRepository.findById(created.getId()).orElseThrow();
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
