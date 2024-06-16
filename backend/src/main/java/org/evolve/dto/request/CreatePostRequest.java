package org.evolve.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Create Post Request")
public class CreatePostRequest {
  private String title;
  private String content;
}
