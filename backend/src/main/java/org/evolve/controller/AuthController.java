package org.evolve.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evolve.dto.response.JwtAuthenticationResponse;
import org.evolve.dto.request.RefreshTokenRequest;
import org.evolve.dto.request.SignInRequest;
import org.evolve.dto.request.SignUpRequest;
import org.evolve.service.auth.AuthenticationService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
@Tag(name = "Sign in")
public class AuthController {
  private final AuthenticationService authenticationService;

  @Operation(summary = "Sign up")
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequest request) {
    return authenticationService.signUp(request);
  }

  @Operation(summary = "Sign in")
  @PostMapping("/sign-in")
  public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
    return authenticationService.signIn(request);
  }

  @Operation(summary = "Refresh access token")
  @PostMapping("/refresh-token")
  public JwtAuthenticationResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
    return authenticationService.refreshToken(request);
  }

  @Operation(summary = "Email verification endpoint")
  @GetMapping("/verify")
  public ResponseEntity<String> verifyUser(@Param("code") String code) {
    if (authenticationService.verify(code)) {
      return new ResponseEntity<>("Your account is verified", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("ERROR", HttpStatus.BAD_REQUEST);
    }
  }
}
