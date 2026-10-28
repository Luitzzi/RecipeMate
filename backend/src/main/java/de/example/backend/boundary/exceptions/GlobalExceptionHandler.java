package de.example.backend.boundary.exceptions;

import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.image.InvalidImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.example.backend.service.exception.chat.ChatNotFoundException;
import de.example.backend.service.exception.chat.ChatResponseFailedException;
import de.example.backend.service.exception.user.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("User not found");
        return problem;
    }

    @ExceptionHandler(ChatResponseFailedException.class)
    ProblemDetail handleChatResponseFailed(ChatResponseFailedException ex) {
        log.error("Chat response failed", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problem.setTitle("Chat response failed unexpectedly");
        return problem;
    }

    @ExceptionHandler(NoAccessException.class)
    ProblemDetail handleNoAccess(NoAccessException ex) {
        log.warn("NoAccess error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("The user has no access to the requested element");
        return problem;
    }

    @ExceptionHandler(InvalidImageException.class)
    ProblemDetail handleInvalidImage(InvalidImageException ex) {
        log.warn("Invalid image request", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid image");
        return problem;
    }
}
