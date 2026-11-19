package de.example.backend.repository.querydsl;

import de.example.backend.entity.recipe.Diet;
import jakarta.annotation.Nullable;

/**
 * Recipe columns needed for compact recipe lists, loaded without the recipe entity.
 */
public record RecipeSummaryProjection(
        Long id,
        String title,
        Integer cookTimeInMinutes,
        Integer difficulty,
        Diet diet,
        @Nullable String coverImageKey) {
}
