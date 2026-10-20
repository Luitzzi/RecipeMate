package de.example.backend.repository;

import de.example.backend.entity.ingredient.IngredientBE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientBE, Long> {

    Optional<IngredientBE> findById(Long id);
}
