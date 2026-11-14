package de.example.backend.service.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String elementDescription) {
        super(elementDescription + " already exists");
    }
}
