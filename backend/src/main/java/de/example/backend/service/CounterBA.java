package de.example.backend.service;

import de.example.backend.boundary.uidto.CounterUiDTO;
import de.example.backend.entity.CounterBE;
import de.example.backend.repository.CounterRepository;
import de.example.backend.service.mapper.CounterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounterBA {

    private static final int INITIAL_COUNTER = 0;
    private final CounterRepository counterRepository;
    private final CounterMapper counterMapper;

    // @Transactional is only necessary when multiple writes are performed.
    // Then everything is done in one transaction.
    // If it is only one write it isn't directly necessary but is also not a problem. Better to have it then forget it
    @Transactional(readOnly = true)
    public Optional<CounterUiDTO> getById(UUID id) {
        return counterRepository.findById(id)
                .map(counterMapper::toDTO);
    }

    public CounterUiDTO create() {
        CounterBE initialCounter = new CounterBE(INITIAL_COUNTER);
        // Use persist instead of save
        // save checks if BE has an id if true calls merge and updates the BE
        // On create this must be avoided else the FE can update arbitrary BEs just by using its id
        // If an id is set on persist it throws an error
        CounterBE created = counterRepository.persist(initialCounter);
        return counterMapper.toDTO(created);
    }

    public CounterUiDTO update(CounterUiDTO counterUiDTO) {
        CounterBE updated = counterRepository.save(
                counterMapper.toBE(counterUiDTO));
        return counterMapper.toDTO(updated);
    }
}
