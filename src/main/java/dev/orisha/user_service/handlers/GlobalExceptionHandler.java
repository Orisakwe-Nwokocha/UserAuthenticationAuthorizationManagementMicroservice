package dev.orisha.user_service.handlers;

import dev.orisha.user_service.dto.responses.errors.ApiErrorResponse;
import dev.orisha.user_service.dto.responses.errors.ErrorResponse;
import dev.orisha.user_service.dto.responses.errors.FieldError;
import dev.orisha.user_service.exceptions.EmailExistsException;
import dev.orisha.user_service.exceptions.ResourceNotFoundException;
import dev.orisha.user_service.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static dev.orisha.user_service.handlers.constants.ErrorConstants.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
//    private GlobalExceptionHandler() {}

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException exception, HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("IllegalState", exception.getMessage(), request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException exception, HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("IllegalState", exception.getMessage(), request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<?> handleEmailExistsException(EmailExistsException exception, HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("EmailExists", exception.getMessage(), request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("UserNotFound", exception.getMessage(), request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("ResourceNotFound", exception.getMessage(), request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException exception,
                                                                   HttpServletRequest request) {
        log(exception.getMessage());
        ApiErrorResponse response = buildErrorResponse("BadRequest", "Invalid or bad user request", request);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setType(DEFAULT_TYPE);
        errorResponse.setTitle(METHOD_NOT_ALLOWED.getReasonPhrase());
        errorResponse.setStatus(SC_METHOD_NOT_ALLOWED);
        errorResponse.setDetail(ex.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMessage(String.format(MESSAGE_KEY, SC_METHOD_NOT_ALLOWED));

        log(METHOD_NOT_ALLOWED.getReasonPhrase(), errorResponse);
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getObjectName(), error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setType(CONSTRAINT_VIOLATION_TYPE);
        errorResponse.setTitle(METHOD_ARGUMENT_NOT_VALID);
        errorResponse.setStatus(SC_BAD_REQUEST);
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMessage(ERR_VALIDATION);
        errorResponse.setFieldErrors(fieldErrors);

        log(METHOD_ARGUMENT_NOT_VALID, errorResponse);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(now());
        errorResponse.setStatus(SC_NOT_FOUND);
        errorResponse.setError(NOT_FOUND.getReasonPhrase());
        errorResponse.setDetail(ex.getMessage());
        errorResponse.setPath(request.getRequestURI());

        log(NOT_FOUND.getReasonPhrase(), errorResponse);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    private static void log(final String message, final ErrorResponse error) {
        log.error("{}: {}", message, error);
    }

    private static void log(final String exceptionMessage) {
        log.error("ERROR: {}", exceptionMessage);
    }

    private static ApiErrorResponse buildErrorResponse(String error, String message, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .responseTime(now())
                .isSuccessful(false)
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
    }

}
