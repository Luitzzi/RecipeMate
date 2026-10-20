package de.example.backend.entity.ingredient;

import de.example.backend.entity.Language;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = IngredientTranslationBE.tableName)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTranslationBE {
    public static final String tableName = "ingredient_translations";

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class IngredientTranslationId implements Serializable {
        private Long ingredientId;
        @Enumerated(EnumType.STRING)
        private Language language;
    }

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
