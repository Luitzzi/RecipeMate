package de.example.backend.repository;

import de.example.backend.entity.recipe.RecipeCuisineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCuisineRepository
        extends JpaRepository<RecipeCuisineEntity, Long>, PersistFragment<RecipeCuisineEntity> {
}
