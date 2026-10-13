package de.example.backend.boundary.uidto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CounterIncrementUiDTO {

    @Positive
    private int increment;
}
