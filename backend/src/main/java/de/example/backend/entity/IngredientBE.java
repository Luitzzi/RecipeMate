package de.example.backend.entity;

import de.example.backend.entity.recipe.RecipeIngredientBE;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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

    @Getter(AccessLevel.NONE)
    @Column(unique = true)
    private String germanName;

    @Getter(AccessLevel.NONE)
    @Column(unique = true)
    private String englishName;

    private String type;

    @OneToMany(mappedBy = "ingredient")
    Set<RecipeIngredientBE> recipeIngredients = new HashSet<>();

    public String getName(Locale locale) {
        return switch (locale.getLanguage()) {
            case "de" -> englishName;
            case "en" -> germanName;
            default -> englishName;
        };
    }
}
