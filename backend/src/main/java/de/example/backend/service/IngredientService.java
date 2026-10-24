package de.example.backend.service;

import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientEntity getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow();
    }

}
