package de.example.backend.entity.recipe;

import java.util.Set;

public enum Diet {
    VEGAN,
    VEGETARIAN,
    PESCATARIAN,
    OMNIVORE;

    /**
     * The diets of the recipes that are found when searching for this diet.
     */
    public Set<Diet> getRecipeDietsMatchingThisDiet() {
        return switch (this) {
            case VEGAN -> Set.of(VEGAN);
            case VEGETARIAN -> Set.of(VEGETARIAN, VEGAN);
            case PESCATARIAN -> Set.of(PESCATARIAN);
            case OMNIVORE -> Set.of(OMNIVORE, PESCATARIAN);
        };
    }
}
