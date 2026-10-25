package de.example.backend.repository;

import de.example.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    // TODO: this is a workaround to seed the mock user into the database.
    //       migrate to a database migration or remove once no longer needed.
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO " + UserEntity.tableName + " (id) VALUES (:id) ON CONFLICT (id) DO NOTHING",
            nativeQuery = true)
    void insertIfAbsent(@Param("id") UUID id);

}
