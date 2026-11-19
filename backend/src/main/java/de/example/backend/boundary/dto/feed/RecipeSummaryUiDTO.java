package de.example.backend.boundary.dto.feed;

import de.example.backend.entity.recipe.Diet;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/* Compact recipe for lists such as the feed. Contains no ingredients and steps */
@Getter
@AllArgsConstructor
public class RecipeSummaryUiDTO {

    private final Long id;

    private final String title;

    private final Integer cookTimeInMinutes;

    /*  1 = very easy, 4 = very hard */
    private final Integer difficulty;

    private final Diet diet;

    /* Presigned download url of the first recipe image. Null if the recipe has no image. */
    @Nullable
    private final String coverImageUrl;
}
