package de.example.backend.service.exception;

public class NoAccessException extends RuntimeException {
    public NoAccessException(String object, String subject) {
        super("User " + subject + " has no access to " + object);
    }
}
