package de.example.backend.service.exception.chat;


public class ChatResponseFailedException extends RuntimeException {
    public ChatResponseFailedException() {
        super("Chat response failed unexpectedly.");
    }
}
