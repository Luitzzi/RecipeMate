package de.example.backend.repository;

import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long>,
        PersistFragment<IngredientEntity> {

    List<IngredientEntity> findAllByCreatedByNull();
}
