package com.example.backendtech.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendtech.exception.FileStorageException;

@Service
public class FileStorageService {

    private final Path uploadDir;

    // Constructor: Inyecta la propiedad 'app.upload.dir' y se asegura de crear el directorio
    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        // 1. Obtener y normalizar la ruta absoluta
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            // 2. Crear el directorio si no existe
            Files.createDirectories(this.uploadDir);
        } catch (Exception ex) {
            // Si falla, lanzamos nuestra excepción personalizada
            throw new FileStorageException("No se pudo crear la carpeta de uploads.", ex);
        }
    }

    /**
     * Guarda el archivo MultipartFile en el disco duro.
     * @param file El archivo subido desde el cliente.
     * @return El nombre único generado para el archivo.
     */
    public String storeFile(MultipartFile file) {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Validación básica para evitar ataques de path traversal
            if (originalName.contains("..")) {
                throw new FileStorageException("Nombre de archivo inválido: " + originalName);
            }

            // 1. Generar nombre único (UUID + nombre original)
            String fileName = UUID.randomUUID().toString() + "_" + originalName;
            
            // 2. Resolver la ubicación final
            Path targetLocation = this.uploadDir.resolve(fileName);
            
            // 3. Copiar el stream del archivo al disco, reemplazando si existe
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo guardar el archivo " + originalName, ex);
        }
    }

    /**
     * Carga el archivo como un recurso para servirlo vía HTTP.
     * @param fileName El nombre único del archivo a cargar.
     * @return El recurso de archivo.
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.uploadDir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("Archivo no encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Archivo no encontrado " + fileName, ex);
        }
    }

    /**
     * Elimina el archivo físico del disco.
     * @param fileName El nombre único del archivo a eliminar.
     * @return true si se eliminó, false si no existía o falló.
     */
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.uploadDir.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // No se lanza una excepción crítica, solo se registra el fallo
            return false; 
        }
    }
}