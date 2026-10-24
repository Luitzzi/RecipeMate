package de.example.backend.dataschema;

import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class IngredientEntityTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private EntityManager entityManager;

    private String spaghettiType = "noodle";
    private String spaghettiGermanName = "Spaghetti Deutsch";
    private String spaghettiEnglishName = "Spaghetti Englisch";
    private IngredientEntity spaghetti;

    @BeforeEach
    void setUp() {
        spaghetti = new IngredientEntity();
        spaghetti.putTranslation(Language.DE, spaghettiGermanName);
        spaghetti.putTranslation(Language.EN, spaghettiEnglishName);
        spaghetti.setType(spaghettiType);
        ingredientRepository.save(spaghetti);
        flushAndClear();
    }

    @Test
    void isIngredientEntityCorrectlyPersisted() {
        IngredientEntity loadedIngredient = ingredientRepository
                .findById(spaghetti.getId())
                .orElseThrow();

        assertThat(loadedIngredient.getName(Language.DE))
                .isEqualTo(spaghettiGermanName);
        assertThat(loadedIngredient.getName(Language.EN))
                .isEqualTo(spaghettiEnglishName);
        assertThat(loadedIngredient.getType())
                .isEqualTo(spaghettiType);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
