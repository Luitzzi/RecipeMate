package de.example.backend.boundary.controller;

import de.example.backend.boundary.uidto.ExampleUiDTO;
import de.example.backend.service.ExampleBA;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/example")
@RequiredArgsConstructor
@Tag(name = "Example", description = "Example controller")
public class ExampleUiController {

    private final ExampleBA exampleBA;

    @GetMapping("/{id}")
    @Operation(summary = "Get example")
    @ApiResponse(responseCode = "200", description = "Example found")
    @ApiResponse(responseCode = "404", description = "Example not found")
    public ResponseEntity<ExampleUiDTO> getById(@PathVariable UUID id) {
        return exampleBA.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Post example")
    public ResponseEntity<ExampleUiDTO> create(@RequestBody ExampleUiDTO exampleUiDTO) {
        ExampleUiDTO created = exampleBA.create(exampleUiDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update counter in example")
    public ResponseEntity<ExampleUiDTO> update(@PathVariable UUID id, @RequestBody ExampleUiDTO exampleUiDTO) {
        ExampleUiDTO updated = exampleBA.update(id, exampleUiDTO);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }
}
