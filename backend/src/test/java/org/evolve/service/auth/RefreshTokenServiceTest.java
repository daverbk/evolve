package org.evolve.service.auth;

import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.RefreshToken;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.evolve.exception.auth.TokenRefreshException;
import org.evolve.repository.RefreshTokenRepository;
import org.evolve.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(FakerConfiguration.class)
@TestPropertySource(properties = {"token.timeout.refresh=7200"})
public class RefreshTokenServiceTest {
  private static UUID uuid;
  private static MockedStatic<UUID> mockedStatic;

  @Value("${token.timeout.refresh}")
  private int expirationSeconds;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @Autowired
  private Faker faker;

  @BeforeAll
  public static void setUp() {
    uuid = UUID.randomUUID();
    mockedStatic = mockStatic(UUID.class);
    when(UUID.randomUUID()).thenReturn(uuid);
  }

  @AfterAll
  public static void tearDown() {
    mockedStatic.close();
  }

  @Test
  public void givenCorrectData_whenGenerateToken_thenCorrectRefreshToken() {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    User user = new User(faker.random().nextLong(), username, email, password,
      true, Role.ROLE_USER, uuid.toString(), null);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    RefreshToken refreshToken = refreshTokenService.generateToken(user);

    assertNotNull(refreshToken);
    assertNotNull(refreshToken.getToken());
    assertEquals(username, refreshToken.getUser().getUsername());
    assertTrue(Duration.between(
      Instant.now(), refreshToken.getExpirationDate()).getSeconds() <= expirationSeconds);

    verify(userRepository, times(1)).findByUsername(username);
    verify(refreshTokenRepository, times(1)).save(any());
  }

  @Test
  public void givenExpiredToken_whenVerifyExpiration_thenTokenRefreshException() {
    RefreshToken expiredToken = new RefreshToken();
    expiredToken.setExpirationDate(Instant.now().minusSeconds(1));
    assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(expiredToken));
    verify(refreshTokenRepository, times(1)).delete(expiredToken);
  }

  @Test
  public void givenValidToken_whenVerifyExpiration_thenNoException() {
    RefreshToken nonExpiredToken = new RefreshToken();
    nonExpiredToken.setExpirationDate(Instant.now().plusSeconds(1));
    assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(nonExpiredToken));
    verify(refreshTokenRepository, never()).delete(nonExpiredToken);
  }

  @Test
  public void givenExistingToken_whenGetByToken_thenTokenFound() {
    RefreshToken token = new RefreshToken();
    token.setToken(uuid.toString());
    when(refreshTokenRepository.findByToken(uuid.toString())).thenReturn(Optional.of(token));

    Optional<RefreshToken> retrievedToken = refreshTokenService.getByToken(uuid.toString());

    assertTrue(retrievedToken.isPresent());
    assertEquals(token, retrievedToken.get());
    verify(refreshTokenRepository, times(1)).findByToken(uuid.toString());
  }

  @Test
  public void givenNonExistingToken_whenGetByToken_thenTokenNotFound() {
    String tokenValue = uuid.toString();
    when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

    Optional<RefreshToken> retrievedToken = refreshTokenService.getByToken(tokenValue);

    assertFalse(retrievedToken.isPresent());
    verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
  }
}
