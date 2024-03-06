package org.evolve.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Refresh token request")
public class RefreshTokenRequest {

  @Schema(description = "Refresh token", example = "550e8400-e29b-41d4-a716-446655440000")
  @Size(min = 36, max = 36, message = "Username must contain from 5 to 50 characters")
  @NotBlank(message = "Refresh token must not be blank")
  private String refreshToken;
}
