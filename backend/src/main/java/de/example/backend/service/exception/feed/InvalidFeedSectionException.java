package de.example.backend.service.exception.feed;

public class InvalidFeedSectionException extends RuntimeException {
    public InvalidFeedSectionException(String message) {
        super(message);
    }
}
