package com.dte.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Catches errors and returns nice JSON responses
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(
    GlobalExceptionHandler.class
  );

  // Handle validation errors
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
    MethodArgumentNotValidException ex
  ) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      String message = error.getDefaultMessage();
      if (message == null) {
        message = "Invalid value";
      }
      fieldErrors.put(error.getField(), message);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now().toString());
    response.put("status", 400);
    response.put("error", "Validation Failed");
    response.put("message", "Some fields have invalid values");
    response.put("fieldErrors", fieldErrors);

    logger.warn("Validation error: {}", fieldErrors);
    return ResponseEntity.badRequest().body(response);
  }

  // Handle bad arguments
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleBadArgument(
    IllegalArgumentException ex
  ) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now().toString());
    response.put("status", 400);
    response.put("error", "Bad Request");
    response.put("message", ex.getMessage());

    logger.warn("Bad argument: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(response);
  }

  // Handle everything else
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now().toString());
    response.put("status", 500);
    response.put("error", "Internal Server Error");
    response.put("message", "Something went wrong. Please try again.");

    logger.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      response
    );
  }
}
