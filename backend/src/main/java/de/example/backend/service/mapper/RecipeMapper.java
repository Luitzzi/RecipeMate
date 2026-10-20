package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.entity.recipe.RecipeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class RecipeMapper implements AbstractMapper<RecipeEntity, RecipeUiDTO> {

    @Override
    public abstract RecipeEntity toEntity(RecipeUiDTO recipeUiDTO);

    @Mapping(source = "owner", target = "", ignore = true)
    @Override
    public abstract RecipeUiDTO toDTO(RecipeEntity entity);
}
