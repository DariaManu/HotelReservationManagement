package com.siemens.backend.service;

public class BusinessException extends RuntimeException {
    public BusinessException(){}
    public BusinessException(String message) {
        super(message);
    }
}
