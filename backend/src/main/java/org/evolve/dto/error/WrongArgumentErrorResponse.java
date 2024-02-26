package org.evolve.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WrongArgumentErrorResponse {
    private int status;
    private String message;
    private long timeStamp;
}
