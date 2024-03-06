package org.evolve.service.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.evolve.entity.RefreshToken;
import org.evolve.exception.auth.TokenRefreshException;
import org.evolve.repository.RefreshTokenRepository;
import org.evolve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Getter
  @Value("${token.timeout.refresh}")
  private int expirationSeconds;

  @Transactional
  public RefreshToken generateToken(UserDetails userDetails) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userRepository.findByUsername(
      userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found.")
    ));
    refreshToken.setExpirationDate(Instant.now().plusSeconds(expirationSeconds));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  @Transactional
  public void verifyExpiration(RefreshToken token) {
    if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
        token.getToken(),
        "Refresh token has expired. Please make a new sign in request."
      );
    }
  }

  public Optional<RefreshToken> getByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }
}
