package de.example.backend.service.exception.ingredient;

public class InvalidIngredientException extends RuntimeException {
    public InvalidIngredientException(String message) {
        super(message);
    }
}
