package de.example.backend.boundary.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.example.backend.boundary.dto.FolderUiDTO;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.FolderService;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@Tag(name = "Folders", description = "Create, update and delete folders of recipes")
public class FolderUiController {

    private final FolderService folderService;

    @PostMapping("/")
    @Operation(summary = "Create a folder")
    public ResponseEntity<FolderUiDTO> create(@RequestBody @Valid FolderUiDTO folder) {
        UserEntity owner = UserService.TEST_USER;
        FolderUiDTO createdFolder = folderService.create(folder, owner);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFolder.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdFolder);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a folder")
    public ResponseEntity<FolderUiDTO> get(@PathVariable Long id) {
        UserEntity owner = UserService.TEST_USER;
        return folderService.get(id, owner)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a folder")
    public ResponseEntity<FolderUiDTO> update(@PathVariable Long id, @RequestBody @Valid FolderUiDTO folder) {
        UserEntity owner = UserService.TEST_USER;
        folder.setId(id);
        return folderService.update(folder, owner)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a folder")
    public ResponseEntity<FolderUiDTO> delete(@PathVariable Long id) {
        UserEntity owner = UserService.TEST_USER;
        return folderService.delete(id, owner)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
