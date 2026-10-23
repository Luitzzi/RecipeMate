package de.example.backend.boundary.uidto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeCuisineUiDTO {

    private Long id;

    private String name;

}
