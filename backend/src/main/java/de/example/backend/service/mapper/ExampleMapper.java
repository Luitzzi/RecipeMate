package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.ExampleUiDTO;
import de.example.backend.entity.ExampleBE;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ExampleMapper implements AbstractMapper<ExampleBE, ExampleUiDTO> {

    @Override
    @Mapping(target = "count", source = "counter")
    public abstract ExampleBE toBE(ExampleUiDTO exampleUiDTO);

    @Override
    @Mapping(target = "counter", source = "count")
    public abstract ExampleUiDTO toDTO(ExampleBE entity);

}
