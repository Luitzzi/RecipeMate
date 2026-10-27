package de.example.backend.boundary.dto.image_storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;

@Getter
@AllArgsConstructor
public class ImageUrlUiDTO {
    private String key;
    private URL url;
}
