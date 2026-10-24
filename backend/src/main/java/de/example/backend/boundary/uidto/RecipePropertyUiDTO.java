package de.example.backend.boundary.uidto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipePropertyUiDTO {

    private Long id;

    @NotBlank
    private String name;
}
