package com.firstbase.employeemanagement.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is used to handle exceptions and send user a message
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ERROR_MESSAGE = "message: %s %n requested uri: %s";
    public static final String STRING_JOIN_DELIMITER = ",";
    public static final String FIELD_TO_ERROR_SEPARATOR = ": ";
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PATH_FOR_ERRORS = "errors {} for path {}";
    private static final String PATH = "path";
    private static final String ERRORS = "error";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String TYPE = "type";

    //handle resource not found exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        return buildExceptionResponseEntity(exception, HttpStatus.NOT_FOUND, request,
                Collections.singletonList(exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        return buildExceptionResponseEntity(exception, status, request,
                Collections.singletonList(exception.getLocalizedMessage()));
    }

    //handle not valid exception
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + FIELD_TO_ERROR_SEPARATOR + error.getDefaultMessage())
                .collect(Collectors.toList());
        return buildExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
    }

    //handle global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(Exception exception, WebRequest request) {
        ResponseStatus responseStatus =
                exception.getClass().getAnnotation(ResponseStatus.class);
        final HttpStatus status =
                responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        final String localizedMessage = exception.getLocalizedMessage();
        final String path = request.getDescription(false);
        String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : status.getReasonPhrase());
        logger.error(String.format(ERROR_MESSAGE, message, path), exception);
        return buildExceptionResponseEntity(exception, status, request, Collections.singletonList(message));
    }

    /**
     * This method is used to build exception response entity
     *
     * @param exception
     * @param status
     * @param request
     * @param errors
     * @return
     */
    private ResponseEntity<Object> buildExceptionResponseEntity(final Exception exception, final HttpStatus status,
                                                                final WebRequest request, final List<String> errors) {
        final Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);
        body.put(TIMESTAMP, Instant.now());
        body.put(STATUS, status.value());
        body.put(ERRORS, errors);
        body.put(TYPE, exception.getClass().getSimpleName());
        body.put(PATH, path);
        body.put(MESSAGE, buildMessageForStatus(status));
        final String errorsMessage = null != errors ?
                errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(STRING_JOIN_DELIMITER))
                : status.getReasonPhrase();
        logger.error(PATH_FOR_ERRORS, errorsMessage, path);
        return new ResponseEntity<>(body, status);
    }

    /**
     * This method is used to build status message
     *
     * @param status
     * @return
     */
    private String buildMessageForStatus(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return INVALID_REQUEST;
            default:
                return status.getReasonPhrase();
        }
    }
}
