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
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;

    private final IngredientService ingredientService;

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

    public List<String> getRecipeTitles(String prefix, @Nullable UserEntity owner) {
        return recipeQueries.findTop50DistinctTitlesStartingWithAndLowDistanceTo(prefix, owner);
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
        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeUiDTO, owner, null);
        List<IngredientEntity> customIngredients = filterForCustomIngredients(recipeEntity.getIngredients());
        ingredientService.createAll(customIngredients);
        RecipeEntity created = recipeRepository.persist(recipeEntity);
        return recipeMapper.toUiDTO(created);
    }

    public List<RecipeUiDTO> createAll(List<RecipeUiDTO> recipes, UserEntity owner) {
        List<RecipeEntity> recipeEntities = recipes.stream()
                .map(recipe -> recipeMapper.toEntity(recipe, owner, recipe.getSourceUrl()))
                .toList();
        List<RecipeIngredientEntity> recipeIngredients = recipeEntities.stream()
                .flatMap(recipe -> recipe.getIngredients().stream())
                .toList();
        List<IngredientEntity> customIngredients = filterForCustomIngredients(recipeIngredients);
        ingredientService.createAll(customIngredients);

        Collection<RecipeEntity> createdRecipes = recipeRepository.persistAll(recipeEntities);
        return createdRecipes.stream()
                .map(recipeMapper::toUiDTO)
                .toList();
    }

    @Transactional
    public Long createFromAi(RecipeAiDTO recipeAiDTO, UserEntity owner) {
        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeAiDTO, owner);
        List<IngredientEntity> customIngredients = filterForCustomIngredients(recipeEntity.getIngredients());
        ingredientService.createAll(customIngredients);
        RecipeEntity created = recipeRepository.persist(recipeEntity);
        return created.getId();
    }

    private List<IngredientEntity> filterForCustomIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        return recipeIngredients.stream()
                .map(RecipeIngredientEntity::getIngredient)
                .filter(ingredient -> ingredient.getId() == null)
                .toList();
    }
}
