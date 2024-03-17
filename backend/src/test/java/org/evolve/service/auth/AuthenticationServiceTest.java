package org.evolve.service.auth;

import jakarta.mail.MessagingException;
import net.datafaker.Faker;
import org.evolve.config.EvolveTestConfiguration;
import org.evolve.dto.request.RefreshTokenRequest;
import org.evolve.dto.request.SignInRequest;
import org.evolve.dto.request.SignUpRequest;
import org.evolve.dto.response.JwtAuthenticationResponse;
import org.evolve.entity.RefreshToken;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.evolve.service.EmailService;
import org.evolve.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EvolveTestConfiguration.class)
@TestPropertySource(properties = {"token.timeout.access=3600", "token.timeout.refresh=7200"})
public class AuthenticationServiceTest {
  private static UUID uuid;

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private EmailService emailService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthenticationService authenticationService;

  @Autowired
  private Faker faker;

  @BeforeAll
  public static void setUp() {
    uuid = UUID.randomUUID();
    mockStatic(UUID.class);
    when(UUID.randomUUID()).thenReturn(uuid);
  }

  @Test
  public void givenCorrectRequest_whenSignUp_thenSuccessful() throws MessagingException, IOException {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    User user = new User(faker.random().nextLong(), username, email, password,
      false, Role.ROLE_USER, uuid.toString());

    SignUpRequest request = new SignUpRequest(username, email, password);

    when(passwordEncoder.encode(any())).thenReturn(password);
    when(userService.create(any())).thenReturn(user);
    doNothing().when(emailService).sendVerificationEmail(user);

    ResponseEntity<String> responseEntity = authenticationService.signUp(request);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Email verification link has been sent to your email.", responseEntity.getBody());
    verify(userService, times(1)).create(any());
  }

  @Test
  public void givenEmailServiceError_whenSignUp_thenEmailServiceError() throws Exception {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    SignUpRequest request = new SignUpRequest(username, email, password);

    when(passwordEncoder.encode(any())).thenReturn(password);
    doThrow(new MessagingException()).when(emailService).sendVerificationEmail(any());

    ResponseEntity<String> responseEntity = authenticationService.signUp(request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals("We ran into an issue, try again please.", responseEntity.getBody());
    verify(emailService, times(1)).sendVerificationEmail(any());
  }

  @Test
  public void givenCorrectRequest_whenSignIn_thenSuccessful() {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String jwt = faker.letterify("????????????????????????????????????????????????0.7");

    User user = new User(faker.random().nextLong(), username, email, password,
      true, Role.ROLE_USER, uuid.toString());

    RefreshToken refreshToken = new RefreshToken(faker.random().nextLong(),
      uuid.toString(), Instant.now().plus(Duration.ofHours(2)), user);

    SignInRequest request = new SignInRequest(username, password);

    JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse(jwt,
      refreshToken.getToken(), jwtService.getExpirationSeconds(),
      refreshTokenService.getExpirationSeconds());

    when(userService.userDetailsService()).thenReturn(userService::getByUsername);
    when(userService.userDetailsService().loadUserByUsername(any())).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn(jwt);
    when(refreshTokenService.generateToken(user)).thenReturn(refreshToken);

    JwtAuthenticationResponse response = authenticationService.signIn(request);

    assertEquals(expectedResponse, response);
    verify(authenticationManager, times(1)).authenticate(any());
    verify(jwtService, times(1)).generateToken(user);
    verify(refreshTokenService, times(1)).generateToken(user);
  }

  @Test
  public void givenCorrectRequest_whenRefreshToken_thenSuccessful() {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String jwt = faker.letterify("????????????????????????????????????????????????0.7");
    String refresh = uuid.toString();

    User user = new User(faker.random().nextLong(), username, email, password,
      true, Role.ROLE_USER, uuid.toString());

    RefreshToken refreshToken = new RefreshToken(faker.random().nextLong(), refresh,
      Instant.now().plus(Duration.ofHours(2)), user);

    RefreshTokenRequest request = new RefreshTokenRequest(refresh);

    JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse(
      jwt, refresh, jwtService.getExpirationSeconds(),
      Duration.between(Instant.now(), refreshToken.getExpirationDate()).getSeconds());

    when(refreshTokenService.getByToken(refresh)).thenReturn(Optional.of(refreshToken));
    when(jwtService.generateToken(user)).thenReturn(jwt);

    JwtAuthenticationResponse response = authenticationService.refreshToken(request);

    assertEquals(expectedResponse, response);
    verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
  }

  @Test
  public void givenIncorrectRefreshToken_whenRefreshToken_thenTokenNotFound() {
    String refreshToken = uuid.toString();
    RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

    when(refreshTokenService.getByToken(refreshToken)).thenReturn(java.util.Optional.empty());

    assertThrows(NoSuchElementException.class, () -> authenticationService.refreshToken(request));
  }
}
