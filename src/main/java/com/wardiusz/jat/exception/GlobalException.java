package com.wardiusz.jat.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {
    private final HttpStatus httpStatus;

    public GlobalException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }
}
