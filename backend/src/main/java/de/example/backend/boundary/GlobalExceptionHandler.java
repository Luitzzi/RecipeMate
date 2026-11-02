package de.example.backend.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.example.backend.service.exception.ChatNotFoundException;
import de.example.backend.service.exception.ChatResponseFailedException;
import de.example.backend.service.exception.InvalidMealPlanException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    ProblemDetail handleChatNotFound(ChatNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Chat not found");
        return problem;
    }

    @ExceptionHandler(ChatResponseFailedException.class)
    ProblemDetail handleChatResponseFailed(ChatResponseFailedException ex) {
        log.error("Chat response failed", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problem.setTitle("Chat response failed unexpectedly");
        return problem;
    }

    @ExceptionHandler(InvalidMealPlanException.class)
    ProblemDetail handleInvalidMealPlan(InvalidMealPlanException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid meal plan");
        return problem;
    }
}
