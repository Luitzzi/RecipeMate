package de.example.backend.service.mapper;

import de.example.backend.boundary.uidto.counter.CounterUiDTO;
import de.example.backend.entity.CounterBE;
import org.mapstruct.Mapper;

@Mapper
public abstract class CounterMapper implements AbstractMapper<CounterBE, CounterUiDTO> {

    @Override
    public abstract CounterUiDTO toDTO(CounterBE counter);

    @Override
    public abstract CounterBE toBE(CounterUiDTO counter);

}
