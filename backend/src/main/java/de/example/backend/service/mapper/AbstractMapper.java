package de.example.backend.service.mapper;

public interface AbstractMapper<Entity, DTO> {

    Entity toEntity(DTO dto);

    DTO toDTO(Entity entity);
}
