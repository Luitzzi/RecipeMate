package de.example.backend.entity.ingredient;

import de.example.backend.entity.Language;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IngredientTranslationId implements Serializable {
    private Long ingredientId;
    @Enumerated(EnumType.STRING)
    private Language language;
}
