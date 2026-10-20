package de.example.backend.repository;

import de.example.backend.entity.ingredient.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    Optional<IngredientEntity> findById(Long id);
}
