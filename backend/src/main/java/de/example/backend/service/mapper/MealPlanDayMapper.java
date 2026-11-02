package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.mealprep.MealPlanDayUiDTO;
import de.example.backend.entity.mealprep.MealPlanDayEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class MealPlanDayMapper {

    @Mapping(target = "recipeIds", source = "recipes")
    public abstract MealPlanDayUiDTO toUiDTO(MealPlanDayEntity entity);

    protected Long toRecipeId(RecipeEntity recipe) {
        return recipe.getId();
    }
}
