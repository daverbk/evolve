package org.evolve.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Post Response")
public class PostResponse {
  private Integer id;
  private String title;
}
