package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeIngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(uses = {IngredientMapper.class})
public abstract class RecipeIngredientMapper
        implements AbstractMapper<RecipeIngredientEntity, RecipeIngredientUiDTO> {

    public abstract RecipeIngredientUiDTO toDTO(RecipeIngredientEntity recipeIngredient, @Context Language language);

    public RecipeIngredientEntity toEntity(RecipeIngredientUiDTO dto) {
        throw new NotImplementedException("Use RecipeEntity#addIngredient to add RecipeIngredients");
    }
}
