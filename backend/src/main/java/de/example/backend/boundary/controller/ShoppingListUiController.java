package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.ShoppingListItemUiDTO;
import de.example.backend.boundary.uidto.ShoppingListUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-list")
@RequiredArgsConstructor
@Tag(name = "Shopping list", description = "Add, update and remove items on the user's shopping list")
public class ShoppingListUiController {

    private final ShoppingListService shoppingListService;

    @GetMapping
    @Operation(summary = "Get the current user's shopping list, creating it if it doesn't exist yet")
    public ShoppingListUiDTO get(@RequestParam Language language) {
        UserEntity user = MockUserUtils.getMockUser();
        return shoppingListService.getOrCreate(user, language);
    }

    @PostMapping("/items")
    @Operation(summary = "Add an ingredient-linked or free-text item to the shopping list")
    public ShoppingListUiDTO addItem(
            @RequestBody @Valid ShoppingListItemUiDTO item,
            @RequestParam Language language
    ) {
        UserEntity user = MockUserUtils.getMockUser();
        boolean hasIngredient = item.getIngredient() != null;
        boolean hasName = item.getName() != null && !item.getName().isBlank();
        if (hasIngredient == hasName)
            throw new IllegalArgumentException("Exactly one of ingredient or name must be set");

        if (hasIngredient)
            return shoppingListService.addIngredientItem(user, item.getIngredient(), item.getAmount(), item.getUnit(), language);
        return shoppingListService.addFreeTextItem(user, item.getName(), item.getAmount(), item.getUnit(), language);
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "Check or uncheck a shopping list item")
    public ShoppingListUiDTO setItemChecked(
            @PathVariable Long itemId,
            @RequestParam boolean checked,
            @RequestParam Language language
    ) {
        UserEntity user = MockUserUtils.getMockUser();
        return shoppingListService.setItemChecked(user, itemId, checked, language);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from the shopping list")
    public ShoppingListUiDTO removeItem(
            @PathVariable Long itemId,
            @RequestParam Language language
    ) {
        UserEntity user = MockUserUtils.getMockUser();
        return shoppingListService.removeItem(user, itemId, language);
    }
}
