package de.example.backend.service.exception;

public class NoAccessException extends RuntimeException {
    public NoAccessException(String requested) {
        super("User has no access to " + requested);
    }
}
