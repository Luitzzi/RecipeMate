package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.RecipeEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(uses = {
        RecipeIngredientMapper.class,
        RecipeStepMapper.class,
        RecipeCuisineMapper.class,
        RecipePropertyMapper.class,
})
public interface RecipeMapper {

    RecipeUiDTO toDTO(RecipeEntity recipe, @Context Language language);

}
