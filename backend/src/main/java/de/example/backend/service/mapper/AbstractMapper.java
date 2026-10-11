package de.example.backend.service.mapper;

public interface AbstractMapper<BE, DTO> {
    public BE toBE(DTO dto);

    public DTO toDTO(BE entity);
}
