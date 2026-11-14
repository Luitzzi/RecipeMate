package de.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.example.backend.entity.FolderEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;

public interface FolderRepository extends JpaRepository<FolderEntity, Long>, PersistFragment<FolderEntity> {

    List<FolderEntity> findAllByOwner(UserEntity user);

    List<FolderEntity> findAllByRecipesIdAndOwner(Long recipeId, UserEntity owner);
}
