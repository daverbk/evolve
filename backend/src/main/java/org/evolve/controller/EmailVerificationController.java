package org.evolve.controller;

import lombok.AllArgsConstructor;
import org.evolve.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EmailVerificationController {
    private final UserService userService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return new ResponseEntity<>("Your account is verified", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ERROR", HttpStatus.BAD_REQUEST);
        }
    }
}
