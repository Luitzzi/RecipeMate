package de.example.backend.boundary.exceptions;

import de.example.backend.service.exception.NoAccessException;
import de.example.backend.service.exception.image.InvalidImageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.example.backend.service.exception.chat.ChatNotFoundException;
import de.example.backend.service.exception.chat.ChatResponseFailedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Log all exceptions Spring MVC raises itself, e.g. unreadable bodies and failed bean validation.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        if (statusCode.isSameCodeAs(HttpStatus.BAD_REQUEST)
                || statusCode.isSameCodeAs(HttpStatus.UNSUPPORTED_MEDIA_TYPE)) {
            log.warn("Rejected invalid request {}: {}", request.getDescription(false), ex.getMessage());
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

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
