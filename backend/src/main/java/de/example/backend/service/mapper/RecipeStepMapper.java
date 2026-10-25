package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.*;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.PrepareStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import org.mapstruct.*;

@Mapper
public abstract class RecipeStepMapper
        implements AbstractMapper<RecipeStepEntity, RecipeStepUiDTO> {

    @SubclassMapping(source = PrepareStepEntity.class, target = PrepareStepUiDTO.class)
    @SubclassMapping(source = OvenStepEntity.class, target = OvenStepUiDTO.class)
    @SubclassMapping(source = StoveStepEntity.class, target = StoveStepUiDTO.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    public abstract RecipeStepUiDTO toDTO(RecipeStepEntity entity);

    public RecipeStepEntity toEntity(RecipeStepUiDTO dto) {
        return switch (dto) {
            case PrepareStepUiDTO prepareStep -> mapPrepareStep(prepareStep);
            case StoveStepUiDTO stoveStep -> mapStoveStep(stoveStep);
            case OvenStepUiDTO ovenStep -> mapOvenStep(ovenStep);
            default -> throw new IllegalArgumentException("Unknown step class");
        };
    }

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract PrepareStepEntity mapPrepareStep(PrepareStepUiDTO prepareStepUiDTO);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract StoveStepEntity mapStoveStep(StoveStepUiDTO stoveStepUiDTO);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract OvenStepEntity mapOvenStep(OvenStepUiDTO ovenStepUiDTO);
}
