package de.example.backend.service;

import de.example.backend.boundary.uidto.ExampleUiDTO;
import de.example.backend.entity.ExampleBE;
import de.example.backend.repository.ExampleRepository;
import de.example.backend.service.mapper.ExampleMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExampleBA {

    private final ExampleRepository exampleRepository;
    private final ExampleMapper exampleMapper;

    public Optional<ExampleUiDTO> getById(UUID id) {
        return exampleRepository.findById(id)
                .map(exampleMapper::toDTO);
    }

    public ExampleUiDTO create(ExampleUiDTO exampleUiDTO) {
        ExampleBE created = exampleRepository.save(
                exampleMapper.toBE(exampleUiDTO));
        return exampleMapper.toDTO(created);
    }

    @Nullable
    public ExampleUiDTO update(UUID id, ExampleUiDTO exampleUiDTO) {
        if (!exampleRepository.existsById(id))
            return null;

        ExampleBE updated = exampleRepository.save(
                exampleMapper.toBE(exampleUiDTO));
        return exampleMapper.toDTO(updated);
    }
}
