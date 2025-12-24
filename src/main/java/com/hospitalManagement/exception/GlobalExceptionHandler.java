package com.hospitalManagement.exception;

import com.hospitalManagement.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * 
 * Centralized exception handling for the entire application.
 * Catches exceptions from all controllers and returns standardized error responses.
 * 
 * Benefits:
 * - Consistent error response format across all endpoints
 * - Proper HTTP status codes for different error types
 * - Detailed validation error messages
 * - Prevents exposing internal error details to clients
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles validation errors from @Valid annotations
     * 
     * Triggered when:
     * - Request body validation fails (e.g., @NotNull, @Email, @Future)
     * - Method parameter validation fails
     * 
     * Returns:
     * - HTTP 400 Bad Request
     * - Map of field names to error messages
     * 
     * @param ex MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // Extract field-level validation errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation failed: " + errors.toString()));
    }
    
    /**
     * Handles constraint violation exceptions
     * 
     * Triggered when:
     * - Path variable or request parameter validation fails
     * - Custom validation constraints are violated
     * 
     * @param ex ConstraintViolationException
     * @return ResponseEntity with validation error message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation error: " + ex.getMessage()));
    }
    
    /**
     * Handles illegal argument exceptions
     * 
     * Triggered when:
     * - Invalid input parameters are provided
     * - Entity not found (e.g., patient/doctor not found)
     * - Business rule violations (e.g., email already exists)
     * 
     * @param ex IllegalArgumentException
     * @return ResponseEntity with error message (HTTP 400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * Handles illegal state exceptions
     * 
     * Triggered when:
     * - Business logic violations (e.g., doctor unavailable, appointment conflict)
     * - Invalid state transitions (e.g., cancelling already cancelled appointment)
     * 
     * @param ex IllegalStateException
     * @return ResponseEntity with error message (HTTP 409 Conflict)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(
            IllegalStateException ex) {
        // HTTP 409 Conflict indicates a conflict with current state of the resource
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * Handles all other unexpected exceptions
     * 
     * Catches any exception not handled by specific handlers above.
     * This prevents exposing internal error details to clients.
     * 
     * In production, you might want to log the full exception stack trace
     * while returning a generic error message to the client.
     * 
     * @param ex Any unexpected exception
     * @return ResponseEntity with generic error message (HTTP 500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        // Log the full exception for debugging (not shown here)
        // In production, use a logging framework like SLF4J/Logback
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}

