package de.example.backend.repository;

import static de.example.backend.entity.recipe.RecipeImageEntity.ImageUploadStatus;

import de.example.backend.entity.recipe.RecipeImageEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RecipeImageRepository extends JpaRepository<RecipeImageEntity, Long>, PersistFragment<RecipeImageEntity> {
    boolean existsByKeyAndCreatedBy(String key, UserEntity createdBy);

    Set<RecipeImageEntity> findAllByKeyInAndCreatedByAndStatus(
            Collection<String> keys, UserEntity createdBy,
            RecipeImageEntity.ImageUploadStatus status);

    List<RecipeImageEntity> findAllByCreatedAtBeforeAndStatus(Instant cutoff, ImageUploadStatus status);

}
