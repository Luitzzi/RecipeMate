package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.*;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.PrepareStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper
public abstract class RecipeStepMapper
        implements AbstractMapper<RecipeStepEntity, RecipeStepUiDTO> {

    @SubclassMapping(source = PrepareStepEntity.class, target = PrepareStepUiDTO.class)
    @SubclassMapping(source = OvenStepEntity.class, target = OvenStepUiDTO.class)
    @SubclassMapping(source = StoveStepEntity.class, target = StoveStepUiDTO.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    public abstract RecipeStepUiDTO toDTO(RecipeStepEntity entity);

    @SubclassMapping(source = PrepareStepUiDTO.class, target = PrepareStepEntity.class)
    @SubclassMapping(source = OvenStepUiDTO.class, target = OvenStepEntity.class)
    @SubclassMapping(source = StoveStepUiDTO.class, target = StoveStepEntity.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    public abstract RecipeStepEntity toEntity(RecipeStepUiDTO dto);
}
