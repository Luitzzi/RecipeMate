package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.*;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.*;
import de.example.backend.service.RecipeCuisineService;
import de.example.backend.service.RecipePropertyService;
import de.example.backend.service.RecipeService;
import jakarta.annotation.Nullable;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Mapper(uses = {
        RecipeIngredientMapper.class,
        RecipeStepMapper.class,
        RecipeCuisineMapper.class,
        RecipePropertyMapper.class,
})
public abstract class RecipeMapper {

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private RecipeStepMapper recipeStepMapper;

    @Autowired
    private RecipeCuisineMapper recipeCuisineMapper;

    @Autowired
    private RecipePropertyMapper recipePropertyMapper;

    public abstract RecipeUiDTO toDTO(RecipeEntity entity, @Context Language language);

    /**
     * Maps a RecipeUiDTO to a RecipeEntity
     * Important aspects:
     * - Owner must be loaded from the token
     * - SourceUrl should only be managed by the backend.
     */
    public RecipeEntity toEntity(RecipeUiDTO dto, UserEntity owner, @Nullable String existingSourceUrl) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setId(dto.getId());
        addIngredients(recipeEntity, dto.getIngredients(), owner);
        addSteps(recipeEntity, dto.getSteps());
        addCuisineTag(recipeEntity, dto.getCuisines());
        addPropertyTag(recipeEntity, dto.getProperties());

        recipeEntity.setOwner(owner);
        recipeEntity.setTitle(dto.getTitle());
        recipeEntity.setDescription(dto.getDescription());
        recipeEntity.setCookTimeInMinutes(dto.getCookTimeInMinutes());
        recipeEntity.setDifficulty(dto.getDifficulty());
        recipeEntity.setDiet(dto.getDiet());
        recipeEntity.setNote(dto.getNote());
        recipeEntity.setSourceUrl(existingSourceUrl);
        recipeEntity.addImageKeys(dto.getImageKeys());
        return recipeEntity;
    }

    private void addIngredients(
            RecipeEntity recipeEntity,
            List<RecipeIngredientUiDTO> recipeIngredientUiDTOS,
            UserEntity owner
    ) {
        int position = 0;
        for (RecipeIngredientUiDTO recipeIngredientUiDTO : recipeIngredientUiDTOS) {
            IngredientEntity ingredientEntity = ingredientMapper.toEntity(
                    recipeIngredientUiDTO.getIngredient(), owner);
            recipeEntity.addIngredient(
                    ingredientEntity,
                    recipeIngredientUiDTO.getAmount(),
                    recipeIngredientUiDTO.getUnit(),
                    position
            );
            position++;
        }
    }

    private void addSteps(
            RecipeEntity recipeEntity,
            List<RecipeStepUiDTO> stepsUiDTOs
    ) {
        int position = 0;
        for (RecipeStepUiDTO stepUiDTO : stepsUiDTOs) {
            RecipeStepEntity stepEntity = recipeStepMapper.toEntity(stepUiDTO);
            recipeEntity.addStep(stepEntity, position);
            position++;
        }
    }

    private void addCuisineTag(
            RecipeEntity recipeEntity,
            List<RecipeCuisineUiDTO> cuisineUiDTOS
    ) {
        List<RecipeCuisineEntity> cuisineEntities = recipeCuisineMapper
                .toEntities(cuisineUiDTOS);
        recipeEntity.addCuisineTags(cuisineEntities);
    }

    private void addPropertyTag(
            RecipeEntity recipeEntity,
            List<RecipePropertyUiDTO> propertyUiDTOS
    ) {
        List<RecipePropertyEntity> propertyEntities = recipePropertyMapper
                .toEntities(propertyUiDTOS);
        recipeEntity.addPropertyTags(propertyEntities);
    }
}
