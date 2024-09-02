package com.app.auth.util.exception;

public class JwtException extends RuntimeException {
  public JwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
