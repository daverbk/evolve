package org.evolve.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserNotFoundErrorResponse {
    private int status;
    private String message;
    private long timeStamp;
}
