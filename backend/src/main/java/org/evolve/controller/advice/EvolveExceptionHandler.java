package org.evolve.controller.advice;

import org.evolve.controller.FriendshipController;
import org.evolve.dto.error.UserNotFoundErrorResponse;
import org.evolve.dto.error.WrongArgumentErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {FriendshipController.class})
public class EvolveExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<UserNotFoundErrorResponse> handleException(UsernameNotFoundException exc) {
        UserNotFoundErrorResponse error =
                UserNotFoundErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(exc.getMessage())
                        .timeStamp(System.currentTimeMillis()).build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<WrongArgumentErrorResponse> handleException(NumberFormatException exc) {
        WrongArgumentErrorResponse error =
                WrongArgumentErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exc.getMessage())
                        .timeStamp(System.currentTimeMillis()).build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
