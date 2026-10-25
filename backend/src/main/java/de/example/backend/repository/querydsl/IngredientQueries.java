package de.example.backend.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.ingredient.QIngredientEntity;
import de.example.backend.entity.ingredient.QIngredientTranslationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IngredientQueries {

    private final JPAQueryFactory queryFactory;

    private final QIngredientEntity ENTITY = QIngredientEntity.ingredientEntity;
    private final QIngredientTranslationEntity INGREDIENT_TRANSLATION_ENTITY = QIngredientTranslationEntity.ingredientTranslationEntity;

    public List<IngredientEntity> get(UUID userId) {
        return queryFactory.select(ENTITY)
                .from(ENTITY)
                .where(ENTITY.createdBy.isNull()
                        .or(ENTITY.createdBy.id.eq(userId)))
                .fetch();
    }

    public Optional<IngredientEntity> getStandingDataByEnglishName(String englishName) {
        IngredientEntity ingredient = queryFactory.select(ENTITY)
                .from(ENTITY)
                .join(ENTITY.translationsByLanguage, INGREDIENT_TRANSLATION_ENTITY)
                .where(ENTITY.createdBy.isNull()
                        .and(INGREDIENT_TRANSLATION_ENTITY.id.language.eq(Language.EN))
                        .and(INGREDIENT_TRANSLATION_ENTITY.translation.eq(englishName)))
                .fetchOne();
        return Optional.ofNullable(ingredient);
    }
}
