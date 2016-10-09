package com.rdu.temp.storage.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author rdu
 * @since 09.10.2016
 */

public class TempFileServiceException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public TempFileServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public TempFileServiceException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
