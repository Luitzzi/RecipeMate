package de.example.backend.boundary.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FolderUiDTO {

    @Nullable
    private Long id;

    @NotEmpty
    private String name;

}
