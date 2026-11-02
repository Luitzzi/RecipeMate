package de.example.backend.service.exception;

public class InvalidMealPlanException extends RuntimeException {
    public InvalidMealPlanException(String message) {
        super(message);
    }
}
