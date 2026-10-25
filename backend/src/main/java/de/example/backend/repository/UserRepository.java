package de.example.backend.repository;

import de.example.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsById(UUID id);

}
