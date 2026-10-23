package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.OvenStepUiDTO;
import de.example.backend.boundary.uidto.PrepareStepUiDTO;
import de.example.backend.boundary.uidto.RecipeStepUiDTO;
import de.example.backend.boundary.uidto.StoveStepUiDTO;
import de.example.backend.entity.recipe.RecipeStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.OvenStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.PrepareStepEntity;
import de.example.backend.entity.recipe.recipe_step_methods.StoveStepEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper
public interface RecipeStepMapper {

    @SubclassMapping(source = PrepareStepEntity.class, target = PrepareStepUiDTO.class)
    @SubclassMapping(source = OvenStepEntity.class, target = OvenStepUiDTO.class)
    @SubclassMapping(source = StoveStepEntity.class, target = StoveStepUiDTO.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    RecipeStepUiDTO toDTO(RecipeStepEntity step);
}
