package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.boundary.dto.user.UserUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.ingredient.InvalidIngredientException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(uses = IngredientMapper.class)
public abstract class UserMapper {

    @Autowired
    private IngredientRepository ingredientRepository;

    public abstract UserUiDTO toUiDTO(UserEntity entity, @Context Language language);

    /**
     * Applies the preferences of the DTO to an existing user.
     * The id is never taken from the DTO, it is always loaded from the token.
     */
    public void updateEntity(UserUiDTO dto, UserEntity entity) {
        entity.setDiet(dto.getDiet());
        entity.setCookingSkill(dto.getCookingSkill());
        entity.replacePreferredCuisines(dto.getPreferredCuisines());
        entity.replaceAllergens(dto.getAllergens());

        List<IngredientEntity> dislikedIngredients = dto.getDislikedIngredients().stream()
                .map(ingredient -> resolveDislikedIngredient(ingredient, entity))
                .toList();
        entity.replaceDislikedIngredients(dislikedIngredients);
    }

    /**
     * Disliked ingredients must already exist. Custom ingredients are not created here,
     * because the user would otherwise reference an unsaved ingredient.
     * Custom ingredients of other users are not accessible.
     */
    private IngredientEntity resolveDislikedIngredient(IngredientUiDTO dto, UserEntity user) {
        if (dto.getId() == null) {
            throw new InvalidIngredientException("Disliked ingredient '" + dto.getName() + "' has no id.");
        }
        IngredientEntity ingredient = ingredientRepository.findById(dto.getId())
                .orElseThrow(() -> new InvalidIngredientException(
                        "Ingredient with ID " + dto.getId() + " not found."));

        UserEntity createdBy = ingredient.getCreatedBy();
        if (createdBy != null && !createdBy.getId().equals(user.getId())) {
            throw new NoAccessException("ingredient");
        }
        return ingredient;
    }
}
