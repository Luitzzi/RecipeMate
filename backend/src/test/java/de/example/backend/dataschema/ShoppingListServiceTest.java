package de.example.backend.dataschema;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.boundary.uidto.ShoppingListItemUiDTO;
import de.example.backend.boundary.uidto.ShoppingListUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.Unit;
import de.example.backend.entity.UserEntity;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.shoppinglist.ShoppingListEntity;
import de.example.backend.entity.shoppinglist.ShoppingListItemEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.ShoppingListRepository;
import de.example.backend.service.IngredientService;
import de.example.backend.service.ShoppingListService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ShoppingListServiceTest {

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DataSchemaTestUtils dataSchemaTestUtils;

    @Autowired
    private EntityManager entityManager;

    private UserEntity owner;
    // Ingredients representing the standing data
    private IngredientEntity spaghetti;
    private IngredientEntity ketchup;

    @BeforeEach
    void setUp() {
        createOwner();
        createMockIngredientStandingData();
        dataSchemaTestUtils.flushAndClear();
    }

    @Test
    void getOrCreate_createsEmptyListOnFirstCall_andReturnsSameListOnSecondCall() {
        ShoppingListUiDTO first = shoppingListService.getOrCreate(owner, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThat(first.getId()).isNotNull();
        assertThat(first.getItems()).isEmpty();
        assertThat(shoppingListRepository.count()).isEqualTo(1);

        ShoppingListUiDTO second = shoppingListService.getOrCreate(owner, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(shoppingListRepository.count()).isEqualTo(1);
    }

    @Test
    void addIngredientItem_withStandingDataIngredient_addsItem() {
        ShoppingListUiDTO updated = shoppingListService.addIngredientItem(
                owner, standingIngredientDto(spaghetti), new BigDecimal("500"), Unit.GRAM, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        ShoppingListEntity loaded = loadShoppingList(updated);
        assertThat(loaded.getItems()).hasSize(1);
        ShoppingListItemEntity item = loaded.getItems().get(0);
        assertThat(item.getIngredient().getId()).isEqualTo(spaghetti.getId());
        assertThat(item.getFreeTextName()).isNull();
        assertThat(item.getAmount()).isEqualByComparingTo("500");
        assertThat(item.getUnit()).isEqualTo(Unit.GRAM);
        assertThat(item.isChecked()).isFalse();
    }

    @Test
    void addIngredientItem_withNewCustomIngredient_persistsIngredientAndAddsItem() {
        long ingredientCountBefore = ingredientRepository.count();

        ShoppingListUiDTO updated = shoppingListService.addIngredientItem(
                owner, customIngredientDto("Saffron"), new BigDecimal("0.5"), Unit.GRAM, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThat(ingredientRepository.count()).isEqualTo(ingredientCountBefore + 1);
        ShoppingListEntity loaded = loadShoppingList(updated);
        assertThat(loaded.getItems()).hasSize(1);
        IngredientEntity ingredient = loaded.getItems().get(0).getIngredient();
        assertThat(ingredient.getId()).isNotNull();
        assertThat(ingredient.getCreatedBy().getId()).isEqualTo(owner.getId());
        assertThat(ingredient.getName(Language.EN)).isEqualTo("Saffron");
    }

    @Test
    void addFreeTextItem_addsItem() {
        ShoppingListUiDTO updated = shoppingListService.addFreeTextItem(
                owner, "Spüli", null, null, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        ShoppingListEntity loaded = loadShoppingList(updated);
        assertThat(loaded.getItems()).hasSize(1);
        ShoppingListItemEntity item = loaded.getItems().get(0);
        assertThat(item.getIngredient()).isNull();
        assertThat(item.getFreeTextName()).isEqualTo("Spüli");

        ShoppingListItemUiDTO itemDto = updated.getItems().get(0);
        assertThat(itemDto.getIngredient()).isNull();
        assertThat(itemDto.getName()).isEqualTo("Spüli");
    }

    @Test
    void setItemChecked_marksItemAsChecked() {
        ShoppingListUiDTO afterAdd = shoppingListService.addFreeTextItem(owner, "Zeva", null, null, Language.EN);
        Long itemId = afterAdd.getItems().get(0).getId();
        dataSchemaTestUtils.flushAndClear();

        ShoppingListUiDTO afterCheck = shoppingListService.setItemChecked(owner, itemId, true, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThat(afterCheck.getItems().get(0).isChecked()).isTrue();
        assertThat(loadShoppingList(afterCheck).getItems().get(0).isChecked()).isTrue();
    }

    @Test
    void removeItem_removesItemAndReindexesPositions() {
        shoppingListService.addFreeTextItem(owner, "Spüli", null, null, Language.EN);
        ShoppingListUiDTO afterSecondAdd = shoppingListService.addFreeTextItem(owner, "Zeva", null, null, Language.EN);
        dataSchemaTestUtils.flushAndClear();
        Long firstItemId = afterSecondAdd.getItems().get(0).getId();

        ShoppingListUiDTO afterRemove = shoppingListService.removeItem(owner, firstItemId, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThat(afterRemove.getItems()).hasSize(1);
        assertThat(afterRemove.getItems().get(0).getName()).isEqualTo("Zeva");
        ShoppingListEntity loaded = loadShoppingList(afterRemove);
        assertThat(loaded.getItems()).extracting(ShoppingListItemEntity::getPosition).containsExactly(0);
    }

    @Test
    void addIngredientItem_duplicateIngredient_throws() {
        shoppingListService.addIngredientItem(owner, standingIngredientDto(spaghetti), null, Unit.GRAM, Language.EN);
        dataSchemaTestUtils.flushAndClear();

        assertThatThrownBy(() ->
                shoppingListService.addIngredientItem(owner, standingIngredientDto(spaghetti), null, Unit.GRAM, Language.EN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private ShoppingListEntity loadShoppingList(ShoppingListUiDTO dto) {
        assertThat(dto.getId()).isNotNull();
        return shoppingListRepository.findById(dto.getId()).orElseThrow();
    }

    private IngredientUiDTO standingIngredientDto(IngredientEntity ingredient) {
        IngredientUiDTO dto = new IngredientUiDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName(Language.EN));
        return dto;
    }

    private IngredientUiDTO customIngredientDto(String name) {
        IngredientUiDTO dto = new IngredientUiDTO();
        dto.setName(name);
        return dto;
    }

    private void createOwner() {
        owner = new UserEntity();
        entityManager.persist(owner);
    }

    private void createMockIngredientStandingData() {
        spaghetti = new IngredientEntity();
        spaghetti.setType("Noodle");
        spaghetti.setCustomName("Spaghetti");

        ketchup = new IngredientEntity();
        ketchup.setType("Sauce");
        ketchup.setCustomName("Ketchup");

        ingredientService.createAll(List.of(spaghetti, ketchup));
    }
}
