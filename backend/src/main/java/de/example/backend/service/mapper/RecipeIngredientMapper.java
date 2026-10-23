package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeIngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(uses = {IngredientMapper.class})
public interface RecipeIngredientMapper {

    RecipeIngredientUiDTO toDTO(RecipeIngredientEntity recipeIngredient, @Context Language language);

}
