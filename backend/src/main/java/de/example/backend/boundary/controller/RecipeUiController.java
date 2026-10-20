package de.example.backend.boundary.controller;

import de.example.backend.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipies")
@RequiredArgsConstructor
@Tag(name = "Recipes", description = "Create, update and delete recipes")
public class RecipeUiController {

    private final RecipeService recipeService;
}
