package org.evolve.controller.advice;

import org.evolve.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EvolveExceptionHandler {
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException exc) {
    return new ResponseEntity<>(getError(HttpStatus.NOT_FOUND, exc), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<ErrorResponse> handleException(NumberFormatException exc) {
    return new ResponseEntity<>(getError(HttpStatus.BAD_REQUEST, exc), HttpStatus.BAD_REQUEST);
  }

  private ErrorResponse getError(HttpStatus httpStatus, Exception exc) {
    return ErrorResponse.builder()
      .status(httpStatus.value())
      .message(exc.getMessage())
      .timeStamp(System.currentTimeMillis()).build();
  }
}
