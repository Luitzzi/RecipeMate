package de.example.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import de.example.backend.boundary.dto.FolderUiDTO;
import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.entity.FolderEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.FolderRepository;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.repository.querydsl.FolderQueries;
import de.example.backend.service.exception.AlreadyExistsException;
import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.NotFoundException;
import de.example.backend.service.mapper.FolderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final FolderQueries folderQueries;
    private final FolderMapper folderMapper;

    private final RecipeRepository recipeRepository;

    private boolean hasAccess(FolderEntity folder, UserEntity subject) {
        return folder.getOwner().getId().equals(subject.getId());
    }

    private void assertHasAccess(FolderEntity folder, UserEntity subject) {
        if (!hasAccess(folder, subject))
            throw new NoAccessException(
                    "Folder " + folder.getId(), subject.getId().toString());
    }

    private void assertFolderNameUnique(String name, UserEntity subject) {
        if (folderQueries.existsByNameAndOwner(name, subject))
            throw new AlreadyExistsException("Folder with name '" + name + "'");
    }

    @Transactional(readOnly = true)
    public Optional<FolderUiDTO> get(Long id, UserEntity subject) {
        return folderRepository.findById(id)
                .map(folder -> {
                    assertHasAccess(folder, subject);
                    return folderMapper.toDTO(folder);
                });
    }

    @Transactional(readOnly = true)
    public PageUiDTO<FolderUiDTO> getPage(UserEntity subject, Pageable pageable) {
        return PageUiDTO.from(folderQueries.getPageOfOwner(subject, pageable)
                .map(folderMapper::toDTO));
    }

    @Transactional
    public FolderUiDTO create(FolderUiDTO folder, UserEntity subject) {
        assertFolderNameUnique(folder.getName(), subject);
        FolderEntity entity = folderMapper.toEntity(folder, subject);
        FolderEntity savedEntity = folderRepository.persist(entity);
        return folderMapper.toDTO(savedEntity);
    }

    @Transactional
    public Optional<FolderUiDTO> delete(Long id, UserEntity subject) {
        return folderRepository.findById(id)
                .map(folder -> {
                    assertHasAccess(folder, subject);
                    folderRepository.delete(folder);
                    return folderMapper.toDTO(folder);
                });
    }

    @Transactional
    public Optional<FolderUiDTO> update(FolderUiDTO folder, UserEntity subject) {
        return folderRepository.findById(folder.getId())
                .map(existingFolder -> {
                    assertHasAccess(existingFolder, subject);
                    if (existingFolder.getName().equals(folder.getName()))
                        return folderMapper.toDTO(existingFolder);
                    assertFolderNameUnique(folder.getName(), subject);
                    existingFolder.setName(folder.getName());
                    FolderEntity savedEntity = folderRepository.persist(existingFolder);
                    return folderMapper.toDTO(savedEntity);
                });
    }

    @Transactional
    public void addRecipe(Long folderId, Long recipeId, UserEntity subject) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Folder with ID " + folderId));
        assertHasAccess(folder, subject);

        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe with ID " + recipeId));
        if (!recipe.getOwner().getId().equals(subject.getId()))
            throw new NoAccessException(
                    "Recipe " + recipe.getId(), subject.getId().toString());

        folder.addRecipe(recipe);
        folderRepository.persist(folder);
    }

    @Transactional
    public void removeRecipe(Long folderId, Long recipeId, UserEntity subject) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Folder with ID " + folderId));
        assertHasAccess(folder, subject);

        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe with ID " + recipeId));
        if (!recipe.getOwner().getId().equals(subject.getId()))
            throw new NoAccessException(
                    "Recipe " + recipe.getId(), subject.getId().toString());

        folder.removeRecipe(recipe);
        folderRepository.persist(folder);
    }

}
