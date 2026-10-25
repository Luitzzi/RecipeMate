package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.ShoppingListItemUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.shoppinglist.ShoppingListItemEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class ShoppingListItemMapper {

    @Autowired
    private IngredientMapper ingredientMapper;

    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "name", source = "freeTextName")
    public abstract ShoppingListItemUiDTO toDTO(ShoppingListItemEntity entity, @Context Language language);

    @AfterMapping
    protected void afterMapping(ShoppingListItemEntity entity, @Context Language language, ShoppingListItemUiDTO dto) {
        if (entity.getIngredient() != null) {
            dto.setIngredient(ingredientMapper.toDTO(entity.getIngredient(), language));
        }
    }
}
