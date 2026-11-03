package de.example.backend.recipe;

import de.example.backend.boundary.dto.mealprep.MealPlanDayUiDTO;
import de.example.backend.entity.mealprep.MealPlanDayEntity;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.recipe.DataSchemaTestUtils;
import de.example.backend.repository.MealPlanDayRepository;
import de.example.backend.service.MealPlanService;
import de.example.backend.service.exception.InvalidMealPlanException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class MealPlanServiceTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 26);

    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private MealPlanDayRepository mealPlanDayRepository;

    @Autowired
    private DataSchemaTestUtils dataSchemaTestUtils;

    @Autowired
    private EntityManager entityManager;

    private UserEntity owner;
    private RecipeEntity lasagne;
    private RecipeEntity curry;

    @BeforeEach
    void setUp() {
        owner = createUser();
        lasagne = createRecipe("Lasagne", owner);
        curry = createRecipe("Curry", owner);
        dataSchemaTestUtils.flushAndClear();
    }

    @Test
    void save_persistsDayWithRecipeOrderAndDuplicates() {
        mealPlanService.save(day(MONDAY, true, lasagne, curry, lasagne), owner);
        dataSchemaTestUtils.flushAndClear();

        MealPlanDayEntity loaded = mealPlanDayRepository.findByOwnerAndDate(owner, MONDAY).orElseThrow();
        assertThat(loaded.isGoShopping()).isTrue();
        assertThat(loaded.getRecipes())
                .extracting(RecipeEntity::getId)
                .containsExactly(lasagne.getId(), curry.getId(), lasagne.getId());
    }

    @Test
    void save_replacesExistingDay() {
        mealPlanService.save(day(MONDAY, true, lasagne), owner);
        dataSchemaTestUtils.flushAndClear();

        MealPlanDayUiDTO saved = mealPlanService.save(day(MONDAY, false, curry), owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(saved.getRecipeIds()).containsExactly(curry.getId());
        assertThat(mealPlanDayRepository.count()).isEqualTo(1);
        MealPlanDayEntity loaded = mealPlanDayRepository.findByOwnerAndDate(owner, MONDAY).orElseThrow();
        assertThat(loaded.isGoShopping()).isFalse();
        assertThat(loaded.getRecipes()).extracting(RecipeEntity::getId).containsExactly(curry.getId());
    }

    @Test
    void save_deletesEmptyDay() {
        mealPlanService.save(day(MONDAY, true, lasagne), owner);
        dataSchemaTestUtils.flushAndClear();

        mealPlanService.save(day(MONDAY, false), owner);
        dataSchemaTestUtils.flushAndClear();

        assertThat(mealPlanDayRepository.findByOwnerAndDate(owner, MONDAY)).isEmpty();
    }

    @Test
    void save_rejectsRecipeOfOtherUser() {
        UserEntity otherUser = createUser();
        RecipeEntity foreignRecipe = createRecipe("Foreign", otherUser);
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() -> mealPlanService.save(day(MONDAY, false, foreignRecipe), owner))
                .isInstanceOf(InvalidMealPlanException.class);
    }

    @Test
    void getRange_returnsOnlyDaysInRange() {
        mealPlanService.save(day(MONDAY.minusDays(1), true), owner);
        mealPlanService.save(day(MONDAY, false, lasagne), owner);
        mealPlanService.save(day(MONDAY.plusDays(6), true, curry), owner);
        mealPlanService.save(day(MONDAY.plusDays(7), true), owner);
        dataSchemaTestUtils.flushAndClear();

        List<MealPlanDayUiDTO> week = mealPlanService.getRange(owner, MONDAY, MONDAY.plusDays(6));

        assertThat(week)
                .extracting(MealPlanDayUiDTO::getDate)
                .containsExactly(MONDAY, MONDAY.plusDays(6));
    }

    @Test
    void getRange_rejectsInvertedRange() {
        assertThatThrownBy(() -> mealPlanService.getRange(owner, MONDAY, MONDAY.minusDays(1)))
                .isInstanceOf(InvalidMealPlanException.class);
    }

    private MealPlanDayUiDTO day(LocalDate date, boolean goShopping, RecipeEntity... recipes) {
        MealPlanDayUiDTO day = new MealPlanDayUiDTO();
        day.setDate(date);
        day.setGoShopping(goShopping);
        for (RecipeEntity recipe : recipes) {
            day.getRecipeIds().add(recipe.getId());
        }
        return day;
    }

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        entityManager.persist(user);
        return user;
    }

    private RecipeEntity createRecipe(String title, UserEntity recipeOwner) {
        RecipeEntity recipe = new RecipeEntity();
        recipe.setOwner(recipeOwner);
        recipe.setTitle(title);
        recipe.setDescription(title);
        recipe.setCookTimeInMinutes(30);
        recipe.setDifficulty(2);
        recipe.setDiet(Diet.OMNIVORE);
        recipe.setNote("");
        entityManager.persist(recipe);
        return recipe;
    }
}
