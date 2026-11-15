package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.boundary.dto.user.UserUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.IngredientRepository;
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
                .map(this::resolveDislikedIngredient)
                .toList();
        entity.replaceDislikedIngredients(dislikedIngredients);
    }

    /**
     * Disliked ingredients must be standing data. Custom ingredients are not allowed,
     * so a preference can never reference an ingredient a user is able to delete.
     */
    private IngredientEntity resolveDislikedIngredient(IngredientUiDTO dto) {
        if (dto.getId() == null) {
            throw new InvalidIngredientException("Disliked ingredient '" + dto.getName() + "' has no id.");
        }
        return ingredientRepository.findByIdAndCreatedByNull(dto.getId())
                .orElseThrow(() -> new InvalidIngredientException(
                        "Ingredient with ID " + dto.getId() + " is not a standing data ingredient."));
    }
}
