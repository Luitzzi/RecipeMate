package de.example.backend.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.ingredient.QIngredientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IngredientQueries {

    private final JPAQueryFactory queryFactory;

    private final QIngredientEntity ENTITY = QIngredientEntity.ingredientEntity;

    public List<IngredientEntity> get(UUID userId) {
        return queryFactory.select(ENTITY)
                .from(ENTITY)
                .where(ENTITY.createdBy.isNull()
                        .or(ENTITY.createdBy.id.eq(userId)))
                .fetch();
    }
}
