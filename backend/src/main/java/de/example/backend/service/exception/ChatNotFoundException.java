package de.example.backend.service.exception;

import java.util.UUID;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(UUID chatId) {
        super("Chat with ID " + chatId + " not found.");
    }
}
