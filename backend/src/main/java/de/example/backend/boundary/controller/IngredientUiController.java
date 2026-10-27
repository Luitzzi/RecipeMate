package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.ingredient.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.service.IngredientService;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients", description = "Makes the standing data of ingredients accessible")
public class IngredientUiController {

    private final IngredientService ingredientService;

    @GetMapping
    @Operation(summary = "Get the standing data ingredients")
    public List<IngredientUiDTO> getAllStandingDataIngredients(
            @RequestParam Language language) {
        return ingredientService.getStandingDataIngredients(language);
    }

    @GetMapping
    @Operation(summary = "Get the standing data ingredients and custom created ones of the user")
    public List<IngredientUiDTO> getAllStandingDataAndCustomIngredients(
            @RequestParam Language language) {
        return ingredientService.getAllWithCustomCreated(
                UserService.TEST_USER.getId(),
                language);
    }
}
