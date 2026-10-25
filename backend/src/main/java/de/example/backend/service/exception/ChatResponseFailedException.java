package de.example.backend.service.exception;


public class ChatResponseFailedException extends RuntimeException {
    public ChatResponseFailedException() {
        super("Chat response failed unexpectedly.");
    }
}
