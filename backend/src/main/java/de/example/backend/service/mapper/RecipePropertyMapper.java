package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipePropertyUiDTO;
import de.example.backend.entity.recipe.RecipePropertyEntity;
import de.example.backend.service.RecipePropertyService;
import org.apache.commons.lang3.NotImplementedException;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class RecipePropertyMapper {

    @Autowired
    private RecipePropertyService recipePropertyService;

    public abstract RecipePropertyUiDTO toDTO(RecipePropertyEntity entity);

    /**
     * Property Tags are standing data.
     * They must not be converted from an UiDTO!
     */
    public List<RecipePropertyEntity> toEntities(List<RecipePropertyUiDTO> dtos) {
        List<Long> propertyIds = dtos.stream()
                .map(RecipePropertyUiDTO::getId)
                .toList();
        return recipePropertyService.getByIds(propertyIds);
    }
}
