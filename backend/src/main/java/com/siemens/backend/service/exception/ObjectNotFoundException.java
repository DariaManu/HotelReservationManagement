package com.siemens.backend.service.exception;

/**
 * Class for exceptions thrown for objects which do not exist.
 */
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(){}
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
