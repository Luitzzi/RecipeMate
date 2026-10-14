package de.example.backend.service.mapper;

public interface AbstractMapper<BE, DTO> {

    BE toBE(DTO dto);

    DTO toDTO(BE entity);
}
