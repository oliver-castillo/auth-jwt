package com.app.auth.util.exception;

import com.app.auth.model.dto.response.OperationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /*
   * Custom Exception Handlers
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<OperationResponse> handleNotFoundException(NotFoundException e) {
    logger.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    return new ResponseEntity<>(new OperationResponse(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<OperationResponse> handleBadRequestException(BadRequestException e) {
    logger.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    return new ResponseEntity<>(new OperationResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<OperationResponse> handleJwtException(JwtException e) {
    logger.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    return new ResponseEntity<>(new OperationResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InternalErrorException.class)
  public ResponseEntity<OperationResponse> handleInternalErrorException(InternalErrorException e) {
    logger.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    return new ResponseEntity<>(new OperationResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /*
   * Spring Exception Handlers
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
    Map<String, String> errors = new LinkedHashMap<>();
    errors.put("message", "Fields not valid");
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
      logger.error(String.format("Field %s: %s", fieldName, errorMessage));
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<OperationResponse> handleValidationException(HttpMessageNotReadableException e) {
    logger.error(e.getMessage());
    return new ResponseEntity<>(new OperationResponse("Body is required"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<OperationResponse> handleValidationException(BadCredentialsException e) {
    logger.error(e.getMessage());
    return new ResponseEntity<>(new OperationResponse("Invalid credentials"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<OperationResponse> handleException(Exception e) {
    logger.warn(e.getClass().getName());
    logger.error(e.getMessage());
    return new ResponseEntity<>(new OperationResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<OperationResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
    logger.error(e.getMessage());
    return new ResponseEntity<>(new OperationResponse("User not found"), HttpStatus.NOT_FOUND);
  }

}
