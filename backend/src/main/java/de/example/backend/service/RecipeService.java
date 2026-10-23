package de.example.backend.service;

import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeRepository recipeRepository;

    public List<RecipeUiDTO> getAll(UserEntity user, Language language) {
        return recipeRepository.findAllByOwner(user).stream()
                .map(recipe ->
                        recipeMapper.toDTO(recipe, language))
                .toList();
    }

    public Optional<RecipeUiDTO> get(Long id, Language language) {
        return recipeRepository.findById(id)
                .map(recipe ->
                        recipeMapper.toDTO(recipe, language));
    }

}
