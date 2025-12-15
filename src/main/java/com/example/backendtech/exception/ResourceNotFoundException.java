package com.example.backendtech.exception;

// Esta excepción se lanzará si un registro no se encuentra en la DB
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}