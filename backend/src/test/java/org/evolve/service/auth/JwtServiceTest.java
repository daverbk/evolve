package org.evolve.service.auth;

import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
@Import(FakerConfiguration.class)
class JwtServiceTest {
  private static UUID uuid;
  private static MockedStatic<UUID> mockedStatic;

  @MockBean
  private JwtService jwtService;

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
  public void givenCorrectData_whenGenerateToken_thenSuccessful() {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String jwt = faker.letterify("????????????????????????????????????????????????0.7");
    User user = new User(faker.random().nextLong(), username, email, password,
      true, Role.ROLE_USER, uuid.toString());

    when(jwtService.generateToken(user)).thenReturn(jwt);
    jwtService.generateToken(user);
    verify(jwtService, times(1)).generateToken(user);
  }

  @Test
  public void givenCorrectData_whenIsTokenValid_thenTrue() {
    String username = faker.internet().username();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String jwt = faker.letterify("????????????????????????????????????????????????0.7");
    User user = new User(faker.random().nextLong(), username, email, password,
      true, Role.ROLE_USER, uuid.toString());

    when(jwtService.isTokenValid(jwt, user)).thenReturn(true);
    jwtService.isTokenValid(jwt, user);
    verify(jwtService, times(1)).isTokenValid(jwt, user);
  }
}
