package org.evolve.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "User Response")
public class UserResponse {
  private String username;
  private String email;
}
