package de.example.backend.entity.ingredient;

import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.RecipeIngredientEntity;
import de.example.backend.entity.user.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import static de.example.backend.entity.ingredient.IngredientTranslationEntity.IngredientTranslationId;

import java.util.*;

@Entity
@Table(name = IngredientEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
public class IngredientEntity {
    public static final String tableName = "ingredients";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id.language")
    private Map<Language, IngredientTranslationEntity> translationsByLanguage = new EnumMap<>(Language.class);

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredientEntity> recipeIngredients = new HashSet<>();

    @Nullable
    @Enumerated(EnumType.STRING)
    private IngredientType type;

    // Ingredients are either standing data seeded into the database,
    // or the user creates a custom ingredient only he should see
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity createdBy;

    public String getName(Language language) {
        return translationsByLanguage
                .get(language)
                .getTranslation();
    }

    public void setCustomName(String customName) {
        Arrays.stream(Language.values())
                .forEach(language -> putTranslation(language, customName));
    }

    public Map<Language, IngredientTranslationEntity> getTranslationsByLanguage() {
        return Collections.unmodifiableMap(translationsByLanguage);
    }

    public void putTranslation(Language language, String translation) {
        IngredientTranslationEntity existing = translationsByLanguage.get(language);
        if (existing != null) {
            existing.setTranslation(translation);
        } else {
            IngredientTranslationId translationId = new IngredientTranslationId(this.id, language);
            translationsByLanguage.put(language, new IngredientTranslationEntity(translationId, this, translation));
        }
    }

    public Set<RecipeIngredientEntity> getRecipeIngredients() {
        return Collections.unmodifiableSet(recipeIngredients);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Ensures x.equals(x) == true
        if (!(o instanceof IngredientEntity that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId()); // Id is null if the entity was not persisted yet. If so comparing it by id is not possible.
    }

    @Override
    public int hashCode() {
        // Id is only assigned on persist -> Constant hashcode
        return IngredientEntity.class.hashCode();
    }
}
