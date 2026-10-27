package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.recipe.ai.RecipeIngredientAiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeIngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { IngredientMapper.class })
public abstract class RecipeIngredientMapper {

    public abstract RecipeIngredientUiDTO toUiDTO(RecipeIngredientEntity recipeIngredient, @Context Language language);

    @Mapping(target = "name", expression = "java(recipeIngredient.getIngredient().getName(language))")
    public abstract RecipeIngredientAiDTO toAiDTO(RecipeIngredientEntity recipeIngredient, @Context Language language);

    public RecipeIngredientEntity toEntity(RecipeIngredientUiDTO dto) {
        throw new NotImplementedException("Use RecipeEntity#addIngredient to add RecipeIngredients");
    }
}
