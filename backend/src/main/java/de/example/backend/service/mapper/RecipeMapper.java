package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.recipe.*;
import de.example.backend.boundary.dto.recipe.ai.RecipeAiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.*;
import de.example.backend.service.image_storage.ImageStorageService;
import jakarta.annotation.Nullable;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;

@Mapper(uses = {
        RecipeIngredientMapper.class,
        RecipeStepMapper.class,
})
public abstract class RecipeMapper {

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private RecipeStepMapper recipeStepMapper;

    @Mapping(target = "imageKeys", source = "images")
    public abstract RecipeUiDTO toUiDTO(RecipeEntity entity, @Context Language language);

    // TODO JPI: how to handle languages? eg. attach language to RecipeDTO?
    public abstract RecipeAiDTO toAiDTO(RecipeEntity entity, @Context Language language);

    /**
     * Maps a RecipeUiDTO to a RecipeEntity
     * Important aspects:
     * - Owner must be loaded from the token
     * - SourceUrl should only be managed by the backend.
     */
    public RecipeEntity toEntity(RecipeUiDTO dto, UserEntity owner, @Nullable String existingSourceUrl) {
        RecipeEntity recipeEntity = toCommonEntity(dto, owner);
        recipeEntity.setId(dto.getId());
        addIngredients(recipeEntity, dto.getIngredients(),
                ingredient -> ingredientMapper.toEntity(ingredient.getIngredient(), owner));
        recipeEntity.setSourceUrl(existingSourceUrl);
        List<RecipeImageEntity> images = imageStorageService.loadImages(dto.getImageKeys(), owner);
        recipeEntity.setImages(images);
        return recipeEntity;
    }

    /**
     * Maps a RecipeAiDTO to a new RecipeEntity
     */
    public RecipeEntity toEntity(RecipeAiDTO dto, UserEntity owner) {
        RecipeEntity recipeEntity = toCommonEntity(dto, owner);
        addIngredients(recipeEntity, dto.getIngredients(),
                ingredient -> ingredientMapper.toEntity(ingredient.getName(), owner));
        recipeEntity.setSourceUrl(dto.getSourceUrl());
        return recipeEntity;
    }

    protected List<String> getImageKeys(List<RecipeImageEntity> images) {
        return images.stream()
                .map(RecipeImageEntity::getKey)
                .toList();
    }

    /**
     * Maps the shared fields from a RecipeDTO.
     */
    private RecipeEntity toCommonEntity(RecipeDTO dto, UserEntity owner) {
        RecipeEntity recipeEntity = new RecipeEntity();
        addSteps(recipeEntity, dto.getSteps());
        recipeEntity.addCuisines(dto.getCuisines());
        recipeEntity.addProperties(dto.getProperties());

        recipeEntity.setOwner(owner);
        recipeEntity.setTitle(dto.getTitle());
        recipeEntity.setDescription(dto.getDescription());
        recipeEntity.setCookTimeInMinutes(dto.getCookTimeInMinutes());
        recipeEntity.setDifficulty(dto.getDifficulty());
        recipeEntity.setDiet(dto.getDiet());
        recipeEntity.setNote(dto.getNote());
        return recipeEntity;
    }

    private <T extends RecipeIngredientDTO> void addIngredients(
            RecipeEntity recipeEntity,
            List<T> recipeIngredientDTOs,
            Function<T, IngredientEntity> resolveIngredient) {
        int position = 0;
        for (T recipeIngredientDTO : recipeIngredientDTOs) {
            recipeEntity.addIngredient(
                    resolveIngredient.apply(recipeIngredientDTO),
                    recipeIngredientDTO.getAmount(),
                    recipeIngredientDTO.getUnit(),
                    position);
            position++;
        }
    }

    private void addSteps(
            RecipeEntity recipeEntity,
            List<RecipeStepDTO> stepDTOs) {
        int position = 0;
        for (RecipeStepDTO stepDTO : stepDTOs) {
            RecipeStepEntity stepEntity = recipeStepMapper.toEntity(stepDTO);
            recipeEntity.addStep(stepEntity, position);
            position++;
        }
    }
}
