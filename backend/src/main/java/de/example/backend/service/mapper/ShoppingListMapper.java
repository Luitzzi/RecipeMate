package de.example.backend.service.mapper;

import de.example.backend.boundary.dto.chat.ShoppingListUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.shoppinglist.ShoppingListEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(uses = ShoppingListItemMapper.class)
public abstract class ShoppingListMapper {

    public abstract ShoppingListUiDTO toDTO(ShoppingListEntity entity, @Context Language language);
}
