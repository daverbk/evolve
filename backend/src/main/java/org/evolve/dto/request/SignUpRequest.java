package org.evolve.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Sign up request")
public class SignUpRequest {

  @Schema(description = "Username", example = "John")
  @Size(min = 4, max = 50, message = "Username must contain from 5 to 50 characters")
  @NotBlank(message = "Username must not be blank")
  private String username;

  @Schema(description = "Email", example = "johndoe@gmail.com")
  @Size(min = 5, max = 255, message = "Email must contain from 5 to 255 characters")
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must follow the format user@example.com")
  private String email;

  @Schema(description = "Password", example = "12312312")
  @Size(max = 255, message = "Password's length must not be greater than 255 characters")
  private String password;
}
