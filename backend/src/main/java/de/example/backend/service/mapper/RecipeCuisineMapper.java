package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.RecipeCuisineUiDTO;
import de.example.backend.entity.recipe.RecipeCuisineEntity;
import de.example.backend.service.RecipeCuisineService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class RecipeCuisineMapper {

    @Autowired
    private RecipeCuisineService recipeCuisineService;

    public abstract RecipeCuisineUiDTO toDTO(RecipeCuisineEntity entity);

    /**
     * Cuisine Tags are standing data.
     * They must not be converted from an UiDTO!
     */
    public List<RecipeCuisineEntity> toEntities(List<RecipeCuisineUiDTO> dtos) {
        List<Long> cuisineIds = dtos.stream()
                .map(RecipeCuisineUiDTO::getId)
                .toList();
        return recipeCuisineService.getByIds(cuisineIds);
    }
}
