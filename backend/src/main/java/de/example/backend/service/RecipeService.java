package de.example.backend.service;

import de.example.backend.entity.UserEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

}
