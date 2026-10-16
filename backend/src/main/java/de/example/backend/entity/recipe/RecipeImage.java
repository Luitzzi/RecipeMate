package de.example.backend.entity.recipe;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class RecipeImage {

    @Column(nullable = false)
    private String storageKey;

}
