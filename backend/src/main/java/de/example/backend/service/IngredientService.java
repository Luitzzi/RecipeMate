package de.example.backend.service;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.querydsl.IngredientQueries;
import de.example.backend.service.mapper.IngredientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientQueries ingredientQueries;

    private final IngredientMapper ingredientMapper;

    public List<IngredientUiDTO> getAll(UUID userId, Language language) {
        List<IngredientEntity> ingredients = ingredientQueries.get(userId);
        return ingredients.stream()
                .map(ingredient ->
                        ingredientMapper.toDTO(ingredient, language))
                .toList();
    }

    /**
     * Load a standing data ingredient
     */
    public IngredientEntity getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow();
    }

    public Collection<IngredientEntity> createAll(List<IngredientEntity> ingredients) {
        return ingredientRepository.persistAll(ingredients);
    }

}
