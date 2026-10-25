package de.example.backend.repository;

import de.example.backend.entity.recipe.RecipeCuisineEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCuisineRepository
        extends JpaRepository<RecipeCuisineEntity, Long>, PersistFragment<RecipeCuisineEntity> {
}
