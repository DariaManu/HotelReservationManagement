package com.siemens.backend.service;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(){}
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
