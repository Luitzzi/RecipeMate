package de.example.backend.service;

import de.example.backend.entity.recipe.RecipeCuisineEntity;
import de.example.backend.repository.RecipeCuisineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeCuisineService {

    private final RecipeCuisineRepository recipeCuisineRepository;

    public List<RecipeCuisineEntity> getByIds(List<Long> ids) {
        return recipeCuisineRepository.findAllById(ids);
    }

}
