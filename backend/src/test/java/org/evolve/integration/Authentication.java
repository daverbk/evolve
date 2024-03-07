package org.evolve.integration;

import jakarta.mail.MessagingException;
import org.evolve.dto.request.SignUpRequest;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.evolve.service.EmailService;
import org.evolve.service.UserService;
import org.evolve.service.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private EmailService emailService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthenticationService authenticationService;

  @Test
  void signUp_Success() throws MessagingException, IOException {
    // Given
    UUID uuid = UUID.randomUUID();
    SignUpRequest signUpRequest = new SignUpRequest("randomname", "email@email.com", "12345678");

    User user = User.builder()
      .username(signUpRequest.getUsername())
      .email(signUpRequest.getEmail())
      .password("password")
      .role(Role.ROLE_USER)
      .verificationCode(uuid.toString())
      .build();

    mockStatic(UUID.class);
    when(UUID.randomUUID()).thenReturn(uuid);
    when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("password");
    when(userService.create(any(User.class))).thenReturn(user);
    doNothing().when(emailService).sendVerificationEmail(user);

    // When
    ResponseEntity<String> responseEntity = authenticationService.signUp(signUpRequest);

    // Then
    assertEquals("Email verification link has been sent to your email.", responseEntity.getBody());
    assertEquals(200, responseEntity.getStatusCode().value());
    verify(userService, times(1)).create(any(User.class));
    verify(emailService, times(1)).sendVerificationEmail(any(User.class));
  }
}
