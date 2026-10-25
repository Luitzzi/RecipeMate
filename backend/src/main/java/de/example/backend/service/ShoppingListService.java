package de.example.backend.service;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.boundary.uidto.ShoppingListUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.Unit;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.shoppinglist.ShoppingListEntity;
import de.example.backend.repository.ShoppingListRepository;
import de.example.backend.service.mapper.IngredientMapper;
import de.example.backend.service.mapper.ShoppingListMapper;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListMapper shoppingListMapper;

    private final IngredientMapper ingredientMapper;

    private final IngredientService ingredientService;

    private final ShoppingListRepository shoppingListRepository;

    @Transactional
    public ShoppingListUiDTO getOrCreate(UserEntity owner, Language language) {
        return shoppingListMapper.toDTO(getOrCreateEntity(owner), language);
    }

    @Transactional
    public ShoppingListUiDTO addIngredientItem(
            UserEntity owner,
            IngredientUiDTO ingredientDto,
            @Nullable BigDecimal amount,
            @Nullable Unit unit,
            Language language
    ) {
        ShoppingListEntity shoppingList = getOrCreateEntity(owner);
        IngredientEntity ingredient = ingredientMapper.toEntity(ingredientDto, owner);
        if (ingredient.getId() == null)
            ingredientService.createAll(List.of(ingredient));
        shoppingList.addIngredientItem(ingredient, amount, unit);
        return shoppingListMapper.toDTO(shoppingList, language);
    }

    @Transactional
    public ShoppingListUiDTO addFreeTextItem(
            UserEntity owner,
            String name,
            @Nullable BigDecimal amount,
            @Nullable Unit unit,
            Language language
    ) {
        ShoppingListEntity shoppingList = getOrCreateEntity(owner);
        shoppingList.addFreeTextItem(name, amount, unit);
        return shoppingListMapper.toDTO(shoppingList, language);
    }

    @Transactional
    public ShoppingListUiDTO removeItem(UserEntity owner, Long itemId, Language language) {
        ShoppingListEntity shoppingList = getOrCreateEntity(owner);
        shoppingList.removeItem(itemId);
        return shoppingListMapper.toDTO(shoppingList, language);
    }

    @Transactional
    public ShoppingListUiDTO setItemChecked(UserEntity owner, Long itemId, boolean checked, Language language) {
        ShoppingListEntity shoppingList = getOrCreateEntity(owner);
        shoppingList.setItemChecked(itemId, checked);
        return shoppingListMapper.toDTO(shoppingList, language);
    }

    private ShoppingListEntity getOrCreateEntity(UserEntity owner) {
        return shoppingListRepository.findByOwner(owner)
                .orElseGet(() -> shoppingListRepository.persist(new ShoppingListEntity(owner)));
    }
}
