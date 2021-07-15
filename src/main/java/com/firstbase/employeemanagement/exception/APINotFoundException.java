package com.firstbase.employeemanagement.exception;

public class APINotFoundException extends RuntimeException {
    public APINotFoundException(String message) {
        super(message);
    }
}
