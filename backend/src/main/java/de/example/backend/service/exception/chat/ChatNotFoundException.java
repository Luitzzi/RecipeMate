package de.example.backend.service.exception.chat;

import java.util.UUID;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(UUID chatId) {
        super("Chat with ID " + chatId + " not found.");
    }
}
