package de.example.backend.repository;

import de.example.backend.entity.UserEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>, PersistFragment<RecipeEntity> {

    List<RecipeEntity> findAllByOwner(UserEntity user);

    boolean existsByOwnerId(UUID ownerId);

}
