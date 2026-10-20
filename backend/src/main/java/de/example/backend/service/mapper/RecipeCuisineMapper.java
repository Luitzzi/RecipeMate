package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeCuisineUiDTO;
import de.example.backend.entity.recipe.RecipeCuisineEntity;
import org.mapstruct.Mapper;

@Mapper
public interface RecipeCuisineMapper {

    RecipeCuisineUiDTO toDTO(RecipeCuisineEntity cuisine);

}
