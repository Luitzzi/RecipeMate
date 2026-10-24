package de.example.backend.repository;

import de.example.backend.entity.recipe.RecipePropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePropertyRepository
        extends JpaRepository<RecipePropertyEntity, Long>, PersistFragment<RecipePropertyEntity> {
}
