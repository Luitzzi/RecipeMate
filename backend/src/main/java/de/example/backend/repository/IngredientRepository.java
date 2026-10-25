package de.example.backend.repository;

import de.example.backend.entity.ingredient.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long>, PersistFragment<IngredientEntity> {
}
