package de.example.backend.boundary.uidto.counter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CounterUiDTO {

    private UUID id;

    private int count;
}
