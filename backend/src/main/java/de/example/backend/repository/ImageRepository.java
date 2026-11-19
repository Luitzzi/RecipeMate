package de.example.backend.repository;

import static de.example.backend.entity.image.ImageEntity.ImageUploadStatus;

import de.example.backend.entity.image.ImageEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<ImageEntity, Long>, PersistFragment<ImageEntity> {
    boolean existsByKeyAndCreatedBy(String key, UserEntity createdBy);

    Set<ImageEntity> findAllByKeyInAndCreatedByAndStatus(
            Collection<String> keys, UserEntity createdBy,
            ImageEntity.ImageUploadStatus status);

    List<ImageEntity> findAllByCreatedAtBeforeAndStatus(Instant cutoff, ImageUploadStatus status);

}
