package de.example.backend.boundary.dto.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestUiDTO {

    @NotBlank
    private String message;

    @NotNull
    @Valid
    private CurrentViewUiDTO currentView;

}
