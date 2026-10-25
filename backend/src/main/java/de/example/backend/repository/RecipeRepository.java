package de.example.backend.repository;

import de.example.backend.entity.UserEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>, PersistFragment<RecipeEntity> {

    List<RecipeEntity> findAllByOwner(UserEntity user);

}
