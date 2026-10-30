package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.image_storage.ImageUrlUiDTO;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.UserService;
import de.example.backend.service.image_storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image storage", description = "Request upload and download urls")
@RequiredArgsConstructor
public class ImageUiController {

    private final ImageStorageService imageStorageService;

    @GetMapping("/uploadUrl")
    @Operation(summary = "Get an upload url.")
    public ImageUrlUiDTO getUploadUrl(
            @RequestParam String contentType) {
        return imageStorageService.createUploadUrl(contentType, UserService.TEST_USER);
    }

    @GetMapping("/downloadUrl")
    @Operation(summary = "Get a download url.")
    public ImageUrlUiDTO getDownloadUrl(
            @RequestParam String key) {
        UserEntity user = UserService.TEST_USER;
        return imageStorageService.createDownloadUrl(key, user);
    }
}
