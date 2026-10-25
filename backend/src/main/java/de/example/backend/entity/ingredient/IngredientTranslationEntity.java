package de.example.backend.entity.ingredient;

import de.example.backend.entity.Language;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = IngredientTranslationEntity.tableName)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTranslationEntity {
    public static final String tableName = "ingredient_translations";

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class IngredientTranslationId implements Serializable {
        private Long ingredientId;
        @Enumerated(EnumType.STRING)
        private Language language;
    }

    @EmbeddedId
    private IngredientTranslationId id = new IngredientTranslationId();

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @MapsId("ingredientId")
    private IngredientEntity ingredient;

    @Setter
    @Column(nullable = false)
    private String translation;

}
