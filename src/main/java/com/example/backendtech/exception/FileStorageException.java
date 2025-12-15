package com.example.backendtech.exception;

// Esta excepción se lanzará cuando falle una operación de archivo (IOException, permisos, etc.)
public class FileStorageException extends RuntimeException {

    // Constructor simple
    public FileStorageException(String message) {
        super(message);
    }

    // Constructor para encapsular la causa original (ej: IOException)
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}