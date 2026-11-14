package de.example.backend.service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String subject) {
        super(subject + " not found.");
    }
}
