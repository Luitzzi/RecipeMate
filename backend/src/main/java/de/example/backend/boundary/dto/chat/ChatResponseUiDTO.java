package de.example.backend.boundary.dto.chat;

import lombok.NonNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatResponseUiDTO {

    public enum ChatResponseStatus {
        SUCCESS,
        REFUSAL,
        ERROR
    }

    @NonNull
    private String message;

    @NonNull
    private ChatResponseStatus status;

}
