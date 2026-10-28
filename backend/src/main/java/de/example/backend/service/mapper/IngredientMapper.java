package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.querydsl.IngredientQueries;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class IngredientMapper {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientQueries ingredientQueries;

    @Mapping(target = "name", expression = "java(entity.getName(language))")
    public abstract IngredientUiDTO toDTO(IngredientEntity entity, @Context Language language);

    public IngredientEntity toEntity(
            IngredientUiDTO dto,
            @Context UserEntity createdBy) {
        if (dto.getId() == null) {
            return toCustomEntity(dto.getName(), createdBy);
        }
        return ingredientRepository.findById(dto.getId())
                .orElseThrow();
    }

    /**
     * Resolves an ingredient by its name in the given language.
     * Unknown ingredients are created as custom ingredients.
     */
    public IngredientEntity toEntity(
            String name,
            @Context UserEntity createdBy) {
        String trimmedName = name.trim();
        return ingredientQueries.getStandingDataByAnyTranslation(trimmedName)
                .orElseGet(() -> toCustomEntity(trimmedName, createdBy));
    }

    /**
     * When the ingredient the user wants to add is not in the standing data,
     * they can create a custom ingredient that is only saved for them.
     */
    private IngredientEntity toCustomEntity(
            String name,
            @Context UserEntity createdBy) {
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.setCustomName(name);
        ingredientEntity.setCreatedBy(createdBy);
        return ingredientEntity;
    }
}
