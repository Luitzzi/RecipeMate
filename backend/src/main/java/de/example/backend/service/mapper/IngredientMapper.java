package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.service.IngredientService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class IngredientMapper {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Mapping(target = "name", expression = "java(entity.getName(language))")
    public abstract IngredientUiDTO toDTO(IngredientEntity entity, @Context Language language);

    public IngredientEntity toEntity(
            IngredientUiDTO dto,
            @Context UserEntity createdBy
    ) {
        if (dto.getId() == null) {
            return getCreatedIngredient(dto, createdBy);
        }
        return ingredientRepository.findById(dto.getId())
                .orElseThrow();
    }

    /**
     * When the ingredient the user wants to add is not in the standing data,
     * he can create a custom ingredient that is only saved for him.
     */
    private IngredientEntity getCreatedIngredient(
            IngredientUiDTO dto,
            @Context UserEntity createdBy
    ) {
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.setCustomName(dto.getName());
        ingredientEntity.setCreatedBy(createdBy);
        return ingredientEntity;
    }
}
