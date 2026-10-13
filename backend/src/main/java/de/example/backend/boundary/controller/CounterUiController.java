package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.CounterIncrementUiDTO;
import de.example.backend.boundary.uidto.CounterSetUiDTO;
import de.example.backend.boundary.uidto.CounterUiDTO;
import de.example.backend.service.CounterBA;
import de.example.backend.service.mapper.CounterMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/counter")
@RequiredArgsConstructor
@Tag(name = "Counter", description = "Example counter controller")
public class CounterUiController {

    private final CounterBA counterBA;
    private final CounterMapper counterMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get counter")
    @ApiResponse(responseCode = "404", description = "Counter not found")
    public ResponseEntity<CounterUiDTO> getById(@PathVariable UUID id) {
        return counterBA.getById(id)
                .map(counterMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create counter starting at 0")
    public CounterUiDTO create() {
        return counterMapper.toDTO(counterBA.create());
    }

    @PostMapping("/{id}/increment")
    @Operation(summary = "Increment counter")
    @ApiResponse(responseCode = "400", description = "Increment not positive")
    @ApiResponse(responseCode = "404", description = "Counter not found")
    public ResponseEntity<CounterUiDTO> increment(@PathVariable UUID id,
            @Valid @RequestBody CounterIncrementUiDTO counterIncrementUiDTO) {
        return counterBA.increment(id, counterIncrementUiDTO)
                .map(counterMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("{id}")
    @Operation(summary = "Set counter")
    @ApiResponse(responseCode = "404", description = "Counter not found")
    public ResponseEntity<CounterUiDTO> set(@PathVariable UUID id,
            @RequestBody CounterSetUiDTO counterSetUiDTO) {
        return counterBA.set(id, counterSetUiDTO)
                .map(counterMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
