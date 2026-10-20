package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IngredientMapper {

    @Mapping(target = "name", expression = "java(ingredient.getName(language))")
    IngredientUiDTO toDTO(IngredientEntity ingredient, @Context Language language);
}
