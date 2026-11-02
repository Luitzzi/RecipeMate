package de.example.backend.service;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.recipe.ai.RecipeAiDTO;
import de.example.backend.boundary.dto.recipe.filter.RecipeFilterUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.repository.querydsl.RecipeQueries;
import de.example.backend.service.mapper.RecipeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;

    private final IngredientService ingredientService;

    private final ImageStorageService imageStorageService;

    private final RecipeRepository recipeRepository;

    private final RecipeQueries recipeQueries;

    public List<RecipeUiDTO> getAll(UserEntity user) {
        return recipeRepository.findAllByOwner(user).stream()
                .map(recipe -> recipeMapper.toUiDTO(recipe))
                .toList();
    }

    public PageUiDTO<RecipeUiDTO> filter(RecipeFilterUiDTO filter, UserEntity user, Pageable pageable) {
        return PageUiDTO.from(recipeQueries.findPageOfOwnerMatchingFilter(filter, user, pageable)
                .map(recipeMapper::toUiDTO));
    }

    public List<String> getRecipeTitles(UserEntity user) {
        return recipeQueries.findDistinctTitlesOfOwnerSortedAlphabetically(user);
    }

    public boolean hasRecipes(UserEntity user) {
        return recipeRepository.existsByOwnerId(user.getId());
    }

    public Optional<RecipeUiDTO> get(Long id) {
        return recipeRepository.findById(id)
                .map(recipe -> recipeMapper.toUiDTO(recipe));
    }

    public Optional<RecipeAiDTO> getForAi(Long id) {
        return recipeRepository.findById(id)
                .map(recipe -> recipeMapper.toAiDTO(recipe));
    }

    @Transactional
    public RecipeUiDTO create(RecipeUiDTO recipeUiDTO, UserEntity owner) {
        if (recipeUiDTO.getId() != null)
            throw new IllegalArgumentException("Recipes must have id == null when created");

        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeUiDTO, owner, null);
        persistCustomIngredients(recipeEntity.getIngredients());
        RecipeEntity created = recipeRepository.persist(recipeEntity);
        return recipeMapper.toUiDTO(created);
    }

    @Transactional
    public Long createFromAi(RecipeAiDTO recipeAiDTO, UserEntity owner) {
        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeAiDTO, owner);
        persistCustomIngredients(recipeEntity.getIngredients());
        RecipeEntity created = recipeRepository.persist(recipeEntity);
        return created.getId();
    }

    private void persistCustomIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        List<IngredientEntity> customIngredients = recipeIngredients.stream()
                .map(RecipeIngredientEntity::getIngredient)
                .filter(ingredient -> ingredient.getId() == null)
                .toList();
        ingredientService.createAll(customIngredients);
    }
}
