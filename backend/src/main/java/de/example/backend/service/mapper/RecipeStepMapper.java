package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.recipe.OvenStepDTO;
import de.example.backend.boundary.dto.recipe.PrepareStepDTO;
import de.example.backend.boundary.dto.recipe.RecipeStepDTO;
import de.example.backend.boundary.dto.recipe.StoveStepDTO;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.OvenStepEntity;
import de.example.backend.entity.recipe.PrepareStepEntity;
import de.example.backend.entity.recipe.StoveStepEntity;
import org.mapstruct.*;

@Mapper
public abstract class RecipeStepMapper
        implements AbstractMapper<RecipeStepEntity, RecipeStepDTO> {

    @SubclassMapping(source = PrepareStepEntity.class, target = PrepareStepDTO.class)
    @SubclassMapping(source = OvenStepEntity.class, target = OvenStepDTO.class)
    @SubclassMapping(source = StoveStepEntity.class, target = StoveStepDTO.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    public abstract RecipeStepDTO toDTO(RecipeStepEntity entity);

    public RecipeStepEntity toEntity(RecipeStepDTO dto) {
        return switch (dto) {
            case PrepareStepDTO prepareStep -> mapPrepareStep(prepareStep);
            case StoveStepDTO stoveStep -> mapStoveStep(stoveStep);
            case OvenStepDTO ovenStep -> mapOvenStep(ovenStep);
            default -> throw new IllegalArgumentException("Unknown step class");
        };
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract PrepareStepEntity mapPrepareStep(PrepareStepDTO prepareStepDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract StoveStepEntity mapStoveStep(StoveStepDTO stoveStepDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    abstract OvenStepEntity mapOvenStep(OvenStepDTO ovenStepDTO);
}
