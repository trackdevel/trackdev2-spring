package org.udg.trackdev.spring.controller.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;
import org.udg.trackdev.spring.entity.ErrorEntity;
import org.udg.trackdev.spring.service.Global;

import java.util.Date;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseEntityExceptionHandler() {
        super();
    }

    // Other exceptions. Add your own exception handling here

    @ExceptionHandler(value={ Exception.class })
    protected ResponseEntity<Object> handleControllerExceptions(Exception ex, WebRequest request) {
        if (ex instanceof ServiceException) {
            return handleExceptionInternal(ex,
                    buildErrorEntity("Service error", HttpStatus.BAD_REQUEST, ex),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        } else if (ex instanceof ControllerException) {
            return handleExceptionInternal(ex,
                    buildErrorEntity("Controller error", HttpStatus.BAD_REQUEST, ex),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        } else if (ex instanceof java.lang.SecurityException) {
            return handleExceptionInternal(ex,
                    buildErrorEntity("Security error", HttpStatus.FORBIDDEN, ex),
                    new HttpHeaders(), HttpStatus.FORBIDDEN, request);
        } else if (ex instanceof EntityNotFound) {
            return handleExceptionInternal(ex,
                    buildErrorEntity("Not found", HttpStatus.NOT_FOUND, ex),
                    new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        } else
            return handleExceptionInternal(ex,
                    buildErrorEntity("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR, ex),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        if (body == null) {
            body = buildErrorForOtherExceptions(ex, status);
        }
        return new ResponseEntity<>(body, headers, status);
    }

    private ErrorEntity buildErrorForOtherExceptions(Exception ex, HttpStatus status) {
        if (ex instanceof BindException || ex instanceof HttpMessageNotReadableException) {
            return buildErrorEntity("Not valid", status, "Some or all fields are invalid");
        } else
            return buildErrorEntity("Unknown error", status, ex);
    }

    private ErrorEntity buildErrorEntity(String errorName, HttpStatus status, String customMessage) {
        return new ErrorEntity(Global.dateFormat.format(new Date()),
                status.value(),
                errorName,
                customMessage);
    }

    private ErrorEntity buildErrorEntity(String errorName, HttpStatus status, Exception ex) {
        return new ErrorEntity(Global.dateFormat.format(new Date()),
                status.value(),
                errorName,
                ex.getMessage());
    }
}
