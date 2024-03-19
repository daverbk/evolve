package org.evolve.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Sign in request")
public class SignInRequest {

  @Schema(description = "Username", example = "John")
  @Size(min = 4, max = 50, message = "Username must contain from 5 to 50 characters")
  @NotBlank(message = "Username must not be blank")
  private String username;

  @Schema(description = "Password", example = "12312312")
  @Size(min = 8, max = 255, message = "Password length must be from 8 to 255 characters")
  @NotBlank(message = "Password must not be blank")
  private String password;
}
