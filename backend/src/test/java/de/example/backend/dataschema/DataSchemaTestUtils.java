package de.example.backend.dataschema;

import de.example.backend.entity.user.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSchemaTestUtils {

    private final EntityManager entityManager;

    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
