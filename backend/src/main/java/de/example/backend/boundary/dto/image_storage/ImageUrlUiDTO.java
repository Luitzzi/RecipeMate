package de.example.backend.boundary.dto.image_storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUrlUiDTO {
    private String key;
    private String url;
}
