package com.example.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomValidationException extends RuntimeException {
    private final HttpStatus statusCode;
    public CustomValidationException(String msg, HttpStatus statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

}
