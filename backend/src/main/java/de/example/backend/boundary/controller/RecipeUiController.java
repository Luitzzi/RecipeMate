package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.RecipeUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.UserEntity;
import de.example.backend.service.RecipeService;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all recipes created by an user in a given language")
    public List<RecipeUiDTO> getAll(@RequestParam Language language) {
        UserEntity user = userService.getMockUser();
        return recipeService.getAll(user, language);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe")
    public ResponseEntity<RecipeUiDTO> get(
            @PathVariable Long id,
            @RequestParam Language language
    ) {
        return recipeService.get(id, language)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a recipe")
    public ResponseEntity<RecipeUiDTO> create(@RequestBody @Valid RecipeUiDTO recipeUiDTO) {
        UserEntity owner = userService.getMockUser();
        RecipeUiDTO created = recipeService.create(recipeUiDTO, owner);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
