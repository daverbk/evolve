package org.evolve.service.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.evolve.dto.JwtAuthenticationResponse;
import org.evolve.dto.RefreshTokenRequest;
import org.evolve.dto.SignInRequest;
import org.evolve.dto.SignUpRequest;
import org.evolve.entity.RefreshToken;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.evolve.service.EmailService;
import org.evolve.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserService userService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public ResponseEntity<String> signUp(SignUpRequest request) {

    User user = User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .role(Role.ROLE_USER)
      .enabled(false)
      .verificationCode(UUID.randomUUID().toString())
      .build();

    try {
      emailService.sendVerificationEmail(user);
    } catch (MessagingException | IOException e) {
      return new ResponseEntity<>(
        "We ran into an issue, try again please.",
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }

    userService.create(user);
    return new ResponseEntity<>(
      "Email verification link has been sent to your email.",
      HttpStatus.OK);
  }

  public JwtAuthenticationResponse signIn(SignInRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
      ));

    UserDetails user = userService
      .userDetailsService()
      .loadUserByUsername(request.getUsername());

    String jwt = jwtService.generateToken(user);
    RefreshToken refreshToken = refreshTokenService.generateToken(user);

    return new JwtAuthenticationResponse(
      jwt,
      refreshToken.getToken(),
      jwtService.getExpirationSeconds(),
      refreshTokenService.getExpirationSeconds());
  }

  public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    RefreshToken refreshToken = refreshTokenService.getByToken(requestRefreshToken)
      .orElseThrow(() -> new NoSuchElementException("Refresh token is not found."));
    refreshTokenService.verifyExpiration(refreshToken);

    return new JwtAuthenticationResponse(
      jwtService.generateToken(refreshToken.getUser()),
      requestRefreshToken,
      jwtService.getExpirationSeconds(),
      Duration.between(Instant.now(), refreshToken.getExpirationDate()).getSeconds()
    );
  }

  public boolean verify(String verificationCode) {
    return userService.verify(verificationCode);
  }
}
