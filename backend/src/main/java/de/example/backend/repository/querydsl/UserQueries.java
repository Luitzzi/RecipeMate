package de.example.backend.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import de.example.backend.entity.QUserEntity;
import de.example.backend.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueries {

    private final JPAQueryFactory queryFactory;

    private final QUserEntity ENTITY = QUserEntity.userEntity;

    public void insert(UserEntity user) {
        queryFactory.insert(ENTITY)
                .columns(ENTITY.id)
                .values(user.getId())
                .execute();
    }

}
