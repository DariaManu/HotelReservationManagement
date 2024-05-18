package com.siemens.backend.service.exception;

/**
 * Class for exceptions thrown for issues in the business logic of the application.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(){}
    public BusinessException(String message) {
        super(message);
    }
}
