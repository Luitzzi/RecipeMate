package de.example.backend.boundary.dto.chat;

import de.example.backend.boundary.dto.chat.ShoppingListItemUiDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingListUiDTO {

    private Long id;

    private List<ShoppingListItemUiDTO> items;
}
