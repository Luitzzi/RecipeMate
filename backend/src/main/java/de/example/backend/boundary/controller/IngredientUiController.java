package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.service.IngredientService;
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
    @Operation(summary = "Get the standing data ingredients and custom created ones of the user")
    public List<IngredientUiDTO> getAll(
            @RequestParam Language language
    ) {
        UserEntity user = MockUserUtils.getMockUser();
        return ingredientService.getAll(user.getId(), language);
    }
}
