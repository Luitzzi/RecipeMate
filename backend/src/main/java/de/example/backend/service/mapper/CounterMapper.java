package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.CounterUiDTO;
import de.example.backend.entity.CounterBE;
import org.mapstruct.Mapper;

@Mapper
public interface CounterMapper {

    CounterUiDTO toDTO(CounterBE counter);

}
