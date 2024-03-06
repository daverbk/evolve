package org.evolve.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
  private int status;
  private String message;
  private long timeStamp;
}
