package de.example.backend.boundary.dto.mail;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDTO {

    @NotNull
    private String to;

    @NotNull
    private String subject;

    @Nullable
    private String body;
}
