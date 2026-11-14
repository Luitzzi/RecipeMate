package de.example.backend.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.example.backend.boundary.dto.FolderUiDTO;
import de.example.backend.entity.FolderEntity;
import de.example.backend.entity.user.UserEntity;

@Mapper
public abstract class FolderMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "owner", source = "owner")
    public abstract FolderEntity toEntity(FolderUiDTO dto, UserEntity owner);

    public abstract FolderUiDTO toDTO(FolderEntity entity);

}
