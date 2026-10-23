package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipePropertyUiDTO;
import de.example.backend.entity.recipe.RecipePropertyEntity;
import org.mapstruct.Mapper;

@Mapper
public interface RecipePropertyMapper {

    RecipePropertyUiDTO toDTO(RecipePropertyEntity property);

}
