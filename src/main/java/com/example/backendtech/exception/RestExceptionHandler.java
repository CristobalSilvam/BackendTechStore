package com.example.backendtech.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Indica que esta clase manejará excepciones globalmente
public class RestExceptionHandler {

    // Maneja ResourceNotFoundException y devuelve 404 NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("timestamp", Instant.now(), "message", ex.getMessage()));
    }

    // Maneja FileStorageException y devuelve 500 INTERNAL SERVER ERROR
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<?> handleFileErr(FileStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("timestamp", Instant.now(), "message", "Error de almacenamiento: " + ex.getMessage()));
    }

    // Maneja cualquier otra excepción no capturada y devuelve 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        // En un entorno real, solo devolverías un mensaje genérico por seguridad
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("timestamp", Instant.now(), "message", "Ocurrió un error inesperado en el servidor."));
    }
}