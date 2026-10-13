package de.example.backend.service;

import de.example.backend.boundary.uidto.CounterIncrementUiDTO;
import de.example.backend.boundary.uidto.CounterSetUiDTO;
import de.example.backend.entity.CounterBE;
import de.example.backend.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounterBA {

    private final CounterRepository counterRepository;

    @Transactional(readOnly = true)
    public Optional<CounterBE> getById(UUID id) {
        return counterRepository.findById(id);
    }

    @Transactional
    public CounterBE create() {
        return counterRepository.save(new CounterBE());
    }

    @Transactional
    public Optional<CounterBE> increment(UUID id, CounterIncrementUiDTO counterIncrementUiDTO) {
        return counterRepository.findById(id)
                .map(counter -> {
                    counter.setCount(counter.getCount() + counterIncrementUiDTO.getIncrement());
                    return counter;
                });
    }

    @Transactional
    public Optional<CounterBE> set(UUID id, CounterSetUiDTO counterSetUiDTO) {
        return counterRepository.findById(id)
                .map(counter -> {
                    counter.setCount(counterSetUiDTO.getCount());
                    return counter;
                });
    }
}
