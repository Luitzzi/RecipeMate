package de.example.backend.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import de.example.backend.entity.FolderEntity;
import de.example.backend.entity.QFolderEntity;
import de.example.backend.entity.recipe.QRecipeEntity;
import de.example.backend.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FolderQueries {

    private final JPAQueryFactory queryFactory;

    private final QFolderEntity ENTITY = QFolderEntity.folderEntity;

    public boolean existsByNameAndOwner(String name, UserEntity owner) {
        return queryFactory.selectOne()
                .from(ENTITY)
                .where(ENTITY.name.eq(name)
                        .and(ENTITY.owner.eq(owner)))
                .fetchFirst() != null;
    }

    public Long getTotalCount(UserEntity owner) {
        return queryFactory.select(ENTITY.count())
                .from(ENTITY)
                .where(ENTITY.owner.eq(owner))
                .fetchOne();
    }

    /**
     * Ordered by name ascending. The sort of the pageable is not applied.
     */
    public Page<FolderEntity> getPageOfOwner(UserEntity owner, Pageable pageable) {
        List<FolderEntity> folders = queryFactory.select(ENTITY)
                .from(ENTITY)
                .where(ENTITY.owner.eq(owner))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(ENTITY.name.asc())
                .fetch();
        return PageableExecutionUtils.getPage(folders, pageable, () -> getTotalCount(owner));
    }

}
