package de.example.backend.service;

import de.example.backend.boundary.uidto.RecipePropertyUiDTO;
import de.example.backend.entity.recipe.RecipePropertyEntity;
import de.example.backend.repository.RecipePropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipePropertyService {

    private final RecipePropertyRepository recipePropertyRepository;

    public List<RecipePropertyEntity> getByIds(List<Long> ids) {
        return recipePropertyRepository.findAllById(ids);
    }
}
