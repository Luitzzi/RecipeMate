package de.example.backend.service;

import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientEntity getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow();
    }

    public Collection<IngredientEntity> createAll(List<IngredientEntity> ingredients) {
        return ingredientRepository.persistAll(ingredients);
    }

}
