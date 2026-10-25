package de.example.backend.boundary.uidto.r2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;

@Getter
@AllArgsConstructor
public class ImageUrlUiDTO {
    private String key;
    private URL url;
}
