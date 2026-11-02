package de.example.backend.service;

import de.example.backend.boundary.dto.mealprep.MealPlanDayUiDTO;
import de.example.backend.entity.mealprep.MealPlanDayEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.MealPlanDayRepository;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.exception.InvalidMealPlanException;
import de.example.backend.service.mapper.MealPlanDayMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanDayMapper mealPlanDayMapper;

    private final MealPlanDayRepository mealPlanDayRepository;

    private final RecipeRepository recipeRepository;

    /**
     * Returns all planned days in the inclusive range. Days without a plan are not included.
     */
    public List<MealPlanDayUiDTO> getRange(UserEntity owner, LocalDate from, LocalDate to) {
        if (from.isAfter(to))
            throw new InvalidMealPlanException("'from' (" + from + ") must not be after 'to' (" + to + ")");

        return mealPlanDayRepository.findAllByOwnerAndDateBetweenOrderByDate(owner, from, to).stream()
                .map(mealPlanDayMapper::toUiDTO)
                .toList();
    }

    /**
     * Creates or replaces the plan of a day. An empty day is deleted.
     */
    @Transactional
    public MealPlanDayUiDTO save(MealPlanDayUiDTO dto, UserEntity owner) {
        List<RecipeEntity> recipes = loadOwnedRecipes(dto.getRecipeIds(), owner);

        MealPlanDayEntity day = mealPlanDayRepository.findByOwnerAndDate(owner, dto.getDate())
                .orElseGet(() -> {
                    MealPlanDayEntity newDay = new MealPlanDayEntity();
                    newDay.setOwner(owner);
                    newDay.setDate(dto.getDate());
                    return newDay;
                });
        day.setGoShopping(dto.isGoShopping());
        day.replaceRecipes(recipes);

        if (day.isEmpty()) {
            if (day.getId() != null)
                mealPlanDayRepository.delete(day);
        } else if (day.getId() == null) {
            mealPlanDayRepository.persist(day);
        }
        return mealPlanDayMapper.toUiDTO(day);
    }

    /**
     * Resolves the recipes in the given order. Duplicates are allowed.
     */
    private List<RecipeEntity> loadOwnedRecipes(List<Long> recipeIds, UserEntity owner) {
        Map<Long, RecipeEntity> recipesById = recipeRepository.findAllById(recipeIds).stream()
                .collect(Collectors.toMap(RecipeEntity::getId, Function.identity()));

        return recipeIds.stream()
                .map(recipeId -> {
                    RecipeEntity recipe = recipesById.get(recipeId);
                    if (recipe == null || !Objects.equals(recipe.getOwner().getId(), owner.getId()))
                        throw new InvalidMealPlanException("Recipe " + recipeId + " does not exist");
                    return recipe;
                })
                .toList();
    }
}
