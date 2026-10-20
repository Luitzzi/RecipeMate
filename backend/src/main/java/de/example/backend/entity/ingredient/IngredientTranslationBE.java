package de.example.backend.entity.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = IngredientTranslationBE.tableName)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTranslationBE {
    public static final String tableName = "ingredient_translations";

    @EmbeddedId
    private IngredientTranslationId id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @MapsId("ingredientId")
    private IngredientBE ingredient;

    @Setter
    @Column(nullable = false)
    private String translation;

}
