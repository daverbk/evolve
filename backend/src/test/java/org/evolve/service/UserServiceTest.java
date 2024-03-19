package org.evolve.service;

import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.User;
import org.evolve.exception.auth.UserAlreadyExistsException;
import org.evolve.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(FakerConfiguration.class)
public class UserServiceTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService userService;

  @Autowired
  private Faker faker;

  @Test
  public void givenExistingUser_whenGetByUsername_thenUserFound() {
    String username = faker.internet().username();
    User user = new User();
    user.setUsername(username);

    when(repository.findByUsername(username)).thenReturn(Optional.of(user));

    User fetchedUser = userService.getByUsername(username);

    assertNotNull(fetchedUser);
    assertEquals(username, fetchedUser.getUsername());
    verify(repository, times(1)).findByUsername(username);
  }

  @Test
  public void givenNonExistingUser_whenGetByUsername_thenUserNotFound() {
    String username = faker.internet().username();
    when(repository.findByUsername(username)).thenReturn(Optional.empty());
    assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(username));
    verify(repository, times(1)).findByUsername(username);
  }

  @Test
  public void givenLoggedInUser_whenExtractUserIdFromSecurityContext_thenUserIdExtracted() {
    User user = new User();
    user.setId(faker.random().nextLong());

    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);

    Long userId = userService.extractUserIdFromSecurityContext();

    assertNotNull(userId);
    assertEquals(user.getId(), userId);
  }

  @Test
  public void givenNewUser_whenCreate_thenUserCreated() {
    String username = faker.internet().username();
    User user = new User();
    user.setUsername(username);

    when(repository.existsByUsername(username)).thenReturn(false);
    when(repository.existsByEmail(any())).thenReturn(false);
    when(repository.save(user)).thenReturn(user);

    User createdUser = userService.create(user);

    assertNotNull(createdUser);
    assertEquals(username, createdUser.getUsername());

    verify(repository, times(1)).existsByUsername(username);
    verify(repository, times(1)).existsByEmail(any());
    verify(repository, times(1)).save(user);
  }

  @Test
  public void givenExistingUser_whenCreate_thenUserAlreadyExistsException() {
    String username = faker.internet().username();
    User user = new User();
    user.setUsername(username);

    when(repository.existsByUsername(username)).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class, () -> userService.create(user));
    verify(repository, times(1)).existsByUsername(username);
    verify(repository, never()).existsByEmail(any());
    verify(repository, never()).save(user);
  }

  @Test
  public void givenExistingUserWithEmail_whenCreate_thenUserAlreadyExistsException() {
    String email = faker.internet().emailAddress();
    User user = new User();
    user.setEmail(email);

    when(repository.existsByUsername(any())).thenReturn(false);
    when(repository.existsByEmail(email)).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class, () -> userService.create(user));
    verify(repository, times(1)).existsByUsername(any());
    verify(repository, times(1)).existsByEmail(email);
    verify(repository, never()).save(user);
  }

  @Test
  public void givenUser_whenSave_thenUserSaved() {
    User user = new User();
    user.setId(faker.random().nextLong());

    when(repository.save(user)).thenReturn(user);

    User savedUser = userService.save(user);

    assertNotNull(savedUser);
    assertEquals(user.getId(), savedUser.getId());
    verify(repository, times(1)).save(user);
  }

  @Test
  public void givenNewUser_whenVerify_thenUserVerified() {
    String verificationCode = faker.internet().uuid();
    User user = new User();
    user.setEnabled(false);
    user.setVerificationCode(verificationCode);

    when(repository.findByVerificationCode(verificationCode)).thenReturn(Optional.of(user));
    when(repository.save(user)).thenReturn(user);

    boolean verified = userService.verify(verificationCode);

    assertTrue(verified);
    assertNull(user.getVerificationCode());
    assertTrue(user.isEnabled());
    verify(repository, times(1)).findByVerificationCode(verificationCode);
    verify(repository, times(1)).save(user);
  }

  @Test
  public void givenEnabledUser_whenVerify_thenUserRemainsVerified() {
    String verificationCode = faker.internet().uuid();
    User user = new User();
    user.setEnabled(true);
    user.setVerificationCode(verificationCode);

    when(repository.findByVerificationCode(verificationCode)).thenReturn(Optional.of(user));

    boolean verified = userService.verify(verificationCode);

    assertFalse(verified);
    assertNotNull(user.getVerificationCode());
    assertTrue(user.isEnabled());
    verify(repository, times(1)).findByVerificationCode(verificationCode);
    verify(repository, never()).save(user);
  }

  @Test
  public void givenNonExistingUser_whenVerify_thenUsernameNotFoundException() {
    String verificationCode = faker.internet().uuid();

    when(repository.findByVerificationCode(verificationCode)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> userService.verify(verificationCode));
    verify(repository, times(1)).findByVerificationCode(verificationCode);
    verify(repository, never()).save(any());
  }
}
