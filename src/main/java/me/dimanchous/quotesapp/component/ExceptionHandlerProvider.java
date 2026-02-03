package me.dimanchous.quotesapp.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerProvider {
    private final Utilities utilities;
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerProvider.class);

    public ExceptionHandlerProvider(Utilities utilities) {
        this.utilities = utilities;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return utilities.buildFailure(
                HttpStatus.METHOD_NOT_ALLOWED,
                List.of(new Utilities.Error(
                        "METHOD_NOT_ALLOWED",
                        "HTTP method not allowed",
                        "The HTTP method '%s' is not supported for this endpoint".formatted(exception.getMethod())
                ))
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        //logger.warn("Malformed request received", exception);

        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            Class<?> targetType = invalidFormatException.getTargetType();

            String errorMessage;
            if (targetType == LocalDateTime.class) {
                errorMessage = "Expected date-time in the format 'yyyy-MM-ddTHH:mm:ss'";
            } else {
                errorMessage = String.format("Expected value of type '%s'", targetType.getSimpleName());
            }

            String globalMessage = String.format("Validation failed for field '%s': %s",
                    fieldName, invalidFormatException.getValue());


            return utilities.buildFailure(
                    HttpStatus.BAD_REQUEST,
                    List.of(new Utilities.Error(
                            "INVALID_VALUE",
                            globalMessage,
                            errorMessage
                    ))
            );
        }

        return utilities.buildFailure(
                HttpStatus.BAD_REQUEST,
                List.of(new Utilities.Error(
                        "MALFORMED_REQUEST",
                        "Malformed request",
                        "The request could not be read or parsed"
                ))
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handlerNotFoundException(NoHandlerFoundException exception) {
        return utilities.buildFailure(HttpStatus.NOT_FOUND, List.of(new Utilities.Error("NOT_FOUND", "Resource not found", "The requested resource was not found")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception exception) {
        logger.error("Unhandled exception occurred", exception);
        return utilities.buildFailure(HttpStatus.INTERNAL_SERVER_ERROR, List.of(new Utilities.Error("INTERNAL_SERVER_ERROR", "Unexpected error", "An unexpected error occurred")));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        return utilities.buildFailure(
                HttpStatus.valueOf(exception.getStatusCode().value()),
                exception.getFieldErrors().stream().map(fieldError -> new Utilities.Error("INVALID_VALUE", "Validation failed for field '%s'".formatted(fieldError.getField()), fieldError.getDefaultMessage())).toList()
        );
    }
}
