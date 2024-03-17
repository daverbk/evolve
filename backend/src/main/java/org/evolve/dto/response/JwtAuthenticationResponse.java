package org.evolve.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with an access token")
public class JwtAuthenticationResponse {

  @Schema(description = "Token type", example = "bearer")
  private String tokenType = "Bearer";

  @Schema(description = "Access token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
  private String accessToken;

  @Schema(description = "Refresh token", example = "550e8400-e29b-41d4-a716-446655440000")
  private String refreshToken;

  @Schema(description = "Access token expiration time in seconds", example = "3600")
  private long expiresIn;

  @Schema(description = "Refresh token expiration in seconds", example = "7200")
  private long refreshExpiresIn;

  public JwtAuthenticationResponse(String accessToken, String refreshToken,
                                   long expiresIn, long extExpiresIn) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
    this.refreshExpiresIn = extExpiresIn;
  }
}
