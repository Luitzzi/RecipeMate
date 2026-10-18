package de.example.backend.boundary.uidto.chat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatUserMessageUiDTO {
    
    private String message;

    @Nullable
    private ChatContext context;
    
}
