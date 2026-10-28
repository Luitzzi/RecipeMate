package de.example.backend.repository.query;

import de.example.backend.entity.user.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueries {

    private final EntityManager entityManager;

    public void insert(UserEntity user) {
        entityManager.createQuery(
                        "insert into UserEntity (id, diet) values (:id, :diet)")
                .setParameter("id", user.getId())
                .setParameter("diet", user.getDiet())
                .executeUpdate();
    }

}
