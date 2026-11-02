package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.recipe.filter.RecipeFilterUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.RecipeService;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipies")
@RequiredArgsConstructor
@Tag(name = "Recipes", description = "Create, update and delete recipes")
public class RecipeUiController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Get all recipes created by an user in a given language")
    public List<RecipeUiDTO> getAll() {
        UserEntity owner = UserService.TEST_USER;
        return recipeService.getAll(owner);
    }

    @GetMapping("/titles")
    @Operation(summary = "Get the distinct titles of all recipes of an user")
    public List<String> getRecipeTitles() {
        UserEntity owner = UserService.TEST_USER;
        return recipeService.getRecipeTitles(owner);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe")
    public ResponseEntity<RecipeUiDTO> get(
            @PathVariable Long id) {
        return recipeService.get(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter for recipes, ordered from newest to oldest")
    public PageUiDTO<RecipeUiDTO> filter(
            @RequestBody @Valid RecipeFilterUiDTO filter,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        UserEntity owner = UserService.TEST_USER;
        return recipeService.filter(filter, owner, pageable);
    }

    @PostMapping
    @Operation(summary = "Create a recipe")
    public ResponseEntity<RecipeUiDTO> create(@RequestBody @Valid RecipeUiDTO recipeUiDTO) {
        UserEntity owner = UserService.TEST_USER;
        RecipeUiDTO created = recipeService.create(recipeUiDTO, owner);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
