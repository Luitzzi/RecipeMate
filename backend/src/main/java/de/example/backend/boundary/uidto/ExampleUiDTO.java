package de.example.backend.boundary.uidto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ExampleUiDTO {

    @Nullable
    private UUID id;

    private int counter;
}
