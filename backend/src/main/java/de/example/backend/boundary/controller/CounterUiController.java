package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.CounterUiDTO;
import de.example.backend.service.CounterBA;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/counter")
@RequiredArgsConstructor
@Tag(name = "Counter", description = "Example counter controller")
public class CounterUiController {

    private final CounterBA counterBA;

    @GetMapping("/{id}")
    @Operation(summary = "Get counter")
    @ApiResponse(responseCode = "404", description = "Counter not found")
    public ResponseEntity<CounterUiDTO> getById(@PathVariable UUID id) {
        return counterBA.getById(id)
                .map((ResponseEntity::ok))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create counter starting at 0")
    public ResponseEntity<CounterUiDTO> create() {
        CounterUiDTO created = counterBA.create();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping
    @Operation(summary = "Increment counter")
    public ResponseEntity<CounterUiDTO> update(@RequestBody CounterUiDTO counterUiDTO) {
        if (counterUiDTO.getId() == null)
            return ResponseEntity.badRequest().build();

        CounterUiDTO updated = counterBA.update(counterUiDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(updated.getId())
                .toUri();
        return ResponseEntity.created(location).body(updated);
    }
}
