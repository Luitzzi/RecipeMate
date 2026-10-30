package de.example.backend.repository;

import de.example.backend.entity.recipe.RecipeImageEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface RecipeImageRepository extends JpaRepository<RecipeImageEntity, Long>, PersistFragment<RecipeImageEntity> {
    boolean existsByKeyAndCreatedBy(String key, UserEntity createdBy);

    Set<RecipeImageEntity> findAllByKeyInAndCreatedByAndStatus(
            Collection<String> keys, UserEntity createdBy,
            RecipeImageEntity.ImageUploadStatus status);

}
