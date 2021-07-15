package com.firstbase.employeemanagement.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class JSONParseException extends RuntimeException {

    public JSONParseException(String message) {
        super(message);
    }

    public JSONParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
