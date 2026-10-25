package de.example.backend.service;

import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.mapper.RecipeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;

    private final IngredientService ingredientService;

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

    public Optional<String> getSourceUrl(Long id) {
        return recipeRepository.findById(id)
                .map(RecipeEntity::getSourceUrl);
    }

    @Transactional
    public RecipeUiDTO create(RecipeUiDTO recipeUiDTO, UserEntity owner) {
        if (recipeUiDTO.getId() != null)
            throw new IllegalArgumentException("Recipes must have id = null when created");

        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeUiDTO, owner, null);
        persistCustomIngredients(recipeEntity.getIngredients());
        RecipeEntity created = recipeRepository.persist(recipeEntity);
        return recipeMapper.toDTO(created, Language.EN);
    }

    private void persistCustomIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        List<IngredientEntity> customIngredients = recipeIngredients.stream()
                .map(RecipeIngredientEntity::getIngredient)
                .filter(ingredient -> ingredient.getId() == null)
                .toList();
        ingredientService.createAll(customIngredients);
    }
}
