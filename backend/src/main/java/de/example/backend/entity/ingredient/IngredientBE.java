package de.example.backend.entity.ingredient;

import de.example.backend.entity.Language;
import de.example.backend.entity.UserBE;
import de.example.backend.entity.recipe.RecipeIngredientBE;
import jakarta.persistence.*;
import lombok.*;

import static de.example.backend.entity.ingredient.IngredientTranslationBE.IngredientTranslationId;

import java.util.*;

@Entity
@Table(name = IngredientBE.tableName)
@Getter
@Setter
@NoArgsConstructor
public class IngredientBE {
    public static final String tableName = "ingredients";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id.language")
    private Map<Language, IngredientTranslationBE> translationsByLanguage = new EnumMap<>(Language.class);

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredientBE> recipeIngredients = new HashSet<>();

    private String type;

    // Ingredients are either standing data seeded into the database,
    // or the user creates a custom ingredient only he should see
    @ManyToOne(fetch = FetchType.LAZY)
    private UserBE createdBy;

    public String getName(Language language) {
        return translationsByLanguage
                .get(language)
                .getTranslation();
    }

    public void setCustomName(String customName) {
        Arrays.stream(Language.values())
                .forEach(language -> putTranslation(language, customName));
    }

    public Map<Language, IngredientTranslationBE> getTranslationsByLanguage() {
        return Collections.unmodifiableMap(translationsByLanguage);
    }

    public void putTranslation(Language language, String translation) {
        IngredientTranslationBE existing = translationsByLanguage.get(language);
        if (existing != null) {
            existing.setTranslation(translation);
        } else {
            IngredientTranslationId translationId = new IngredientTranslationId(this.id, language);
            translationsByLanguage.put(language, new IngredientTranslationBE(translationId, this, translation));
        }
    }

    public void removeTranslation(Language language) {
        translationsByLanguage.remove(language);
    }

    public Set<RecipeIngredientBE> getRecipeIngredients() {
        return Collections.unmodifiableSet(recipeIngredients);
    }
}
