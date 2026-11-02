package de.example.backend.repository.querydsl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import de.example.backend.boundary.dto.recipe.filter.RecipeFilterUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.recipe.Cuisine;
import de.example.backend.entity.recipe.Diet;
import de.example.backend.entity.recipe.QRecipeEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeProperty;
import de.example.backend.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecipeQueries {

    private final JPAQueryFactory queryFactory;

    private final QRecipeEntity ENTITY = QRecipeEntity.recipeEntity;

    /**
     * Ordered from newest to oldest. The sort of the pageable is not applied.
     */
    public Page<RecipeEntity> findPageOfOwnerMatchingFilter(RecipeFilterUiDTO filter, UserEntity owner, Pageable pageable) {
        Predicate ownerAndFilterCriteria = isOwnedByAndMatchesFilter(filter, owner);

        List<RecipeEntity> recipes = queryFactory.selectFrom(ENTITY)
                .where(ownerAndFilterCriteria)
                .orderBy(ENTITY.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(recipes, pageable, () -> getTotalCount(ownerAndFilterCriteria));
    }

    public Long getTotalCount(Predicate ownerAndFilterCriteria) {
        return queryFactory.select(ENTITY.count())
                .from(ENTITY)
                .where(ownerAndFilterCriteria)
                .fetchOne();
    }

    public List<String> findDistinctTitlesOfOwnerSortedAlphabetically(UserEntity owner) {
        return queryFactory.select(ENTITY.title)
                .distinct()
                .from(ENTITY)
                .where(isOwnedBy(owner))
                .orderBy(ENTITY.title.asc())
                .fetch();
    }

    // TODO LGE/JPI: Apply filter.getFolderName() as soon as folders exist in the data model
    private Predicate isOwnedByAndMatchesFilter(RecipeFilterUiDTO filter, UserEntity owner) {
        return Expressions.allOf(
                isOwnedBy(owner),
                titleEqualsExactly(filter.getTitle()),
                languageEquals(filter.getLanguage()),
                dietMatchesSearchedDiet(filter.getDiet()),
                cookTimeIsAtMost(filter.getMaxCookTimeInMinutes()),
                difficultyIsAtMost(filter.getMaxDifficulty()),
                hasAtLeastOneOfCuisines(filter.getCuisines()),
                hasAllProperties(filter.getProperties()),
                containsAllIngredients(filter.getIngredientIds()));
    }

    private BooleanExpression isOwnedBy(UserEntity owner) {
        return ENTITY.owner.eq(owner);
    }

    private BooleanExpression titleEqualsExactly(String title) {
        return StringUtils.hasText(title) ? ENTITY.title.eq(title) : null;
    }

    private BooleanExpression languageEquals(Language language) {
        return language != null ? ENTITY.language.eq(language) : null;
    }

    private BooleanExpression dietMatchesSearchedDiet(Diet searchedDiet) {
        return searchedDiet != null ? ENTITY.diet.in(searchedDiet.getRecipeDietsMatchingThisDiet()) : null;
    }

    private BooleanExpression cookTimeIsAtMost(Integer maxCookTimeInMinutes) {
        return maxCookTimeInMinutes != null ? ENTITY.cookTimeInMinutes.loe(maxCookTimeInMinutes) : null;
    }

    private BooleanExpression difficultyIsAtMost(Integer maxDifficulty) {
        return maxDifficulty != null ? ENTITY.difficulty.loe(maxDifficulty) : null;
    }

    private BooleanExpression hasAtLeastOneOfCuisines(Set<Cuisine> cuisines) {
        return cuisines.isEmpty() ? null : ENTITY.cuisines.any().in(cuisines);
    }

    private BooleanExpression hasAllProperties(Set<RecipeProperty> properties) {
        return Expressions.allOf(properties.stream()
                .map(property -> ENTITY.properties.any().eq(property))
                .toArray(BooleanExpression[]::new));
    }

    private BooleanExpression containsAllIngredients(List<Long> ingredientIds) {
        return Expressions.allOf(ingredientIds.stream()
                .map(ingredientId -> ENTITY.ingredients.any().ingredient.id.eq(ingredientId))
                .toArray(BooleanExpression[]::new));
    }
}
