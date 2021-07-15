package com.firstbase.employeemanagement.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used for resource not found exception
 */
@ResponseStatus
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
