package de.example.backend.service.mapper;

import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import de.example.backend.boundary.dto.folder.FolderUiDTO;
import de.example.backend.entity.folder.FolderEntity;
import de.example.backend.entity.image.ImageEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.image_storage.ImageStorageService;

@Mapper
public abstract class FolderMapper {

    @Autowired
    private ImageStorageService imageStorageService;

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    protected abstract FolderEntity toEntityWithoutImage(FolderUiDTO dto, UserEntity owner);

    public FolderEntity toEntity(FolderUiDTO dto, UserEntity owner) {
        FolderEntity entity = toEntityWithoutImage(dto, owner);
        if (dto.getImageKey() != null) {
            ImageEntity image = imageStorageService
                    .loadImages(List.of(dto.getImageKey()), owner)
                    .getFirst();
            entity.setImage(image);
        }
        return entity;
    }

    @Mapping(target = "imageKey", source = "image.key")
    public abstract FolderUiDTO toDTO(FolderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    protected abstract void updateEntityWithoutImage(FolderUiDTO dto, @MappingTarget FolderEntity entity);

    public void updateEntity(FolderUiDTO dto, FolderEntity entity, UserEntity owner) {
        updateEntityWithoutImage(dto, entity);

        String newKey = dto.getImageKey();
        String currentKey = entity.getImage() != null ? entity.getImage().getKey() : null;
        if (Objects.equals(newKey, currentKey))
            return;

        ImageEntity newImage = null;
        if (newKey != null)
            newImage = imageStorageService.loadImage(newKey, owner);
        entity.setImage(newImage);
    }

}
