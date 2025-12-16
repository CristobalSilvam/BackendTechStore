package com.example.backendtech.config; 

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendtech.model.Producto;
import com.example.backendtech.repository.ProductoRepository;
import com.example.backendtech.service.FileStorageService;

//@Component
public class ImageMigrationRunner implements CommandLineRunner {    

    private final ProductoRepository productoRepository;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate; // Cliente HTTP de Spring

    // Inyección de dependencias
    public ImageMigrationRunner(ProductoRepository productoRepository, FileStorageService fileStorageService) {
        this.productoRepository = productoRepository;
        this.fileStorageService = fileStorageService;
        this.restTemplate = new RestTemplate(); // Inicializa el cliente HTTP
    }

    // Este método se ejecuta automáticamente después de que la aplicación inicia
    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- INICIANDO PROCESO DE MIGRACIÓN DE IMÁGENES ANTIGUAS ---");
        
        List<Producto> productos = productoRepository.findAll();
        int migradas = 0;

        for (Producto producto : productos) {
            String valorImagen = producto.getImagen();
            
            // Verificamos si es una URL externa (si empieza con http o https)
            if (valorImagen != null && valorImagen.toLowerCase().startsWith("http")) { 
                try {
                    // 1. Descargar la imagen de la URL externa
                    byte[] imagenBytes = descargarImagenDesdeUrl(valorImagen); 
                    
                    // 2. Crear un archivo MultipartFile temporal para usar FileStorageService
                    String originalFileName = valorImagen.substring(valorImagen.lastIndexOf('/') + 1);
                    MultipartFile tempFile = crearMultipartFileDesdeBytes(imagenBytes, originalFileName);
                    
                    // 3. Guardar el archivo localmente y obtener el nuevo nombre único
                    String nuevoNombreArchivo = fileStorageService.storeFile(tempFile); 
                    
                    // 4. Actualizar la base de datos
                    producto.setImagen(nuevoNombreArchivo);
                    productoRepository.save(producto);
                    migradas++;
                    
                    System.out.println("MIGRADO con éxito: ID " + producto.getId() + " - " + nuevoNombreArchivo);
                } catch (Exception e) {
                    System.err.println("!! FALLO MIGRACIÓN: ID " + producto.getId() + ", URL: " + valorImagen + ". Error: " + e.getMessage());
                }
            }
        }
        
        if (migradas > 0) {
             System.out.println("--- MIGRACIÓN COMPLETA: " + migradas + " imágenes actualizadas. ---");
        } else {
             System.out.println("--- MIGRACIÓN COMPLETA: No se encontraron URLs externas para migrar. ---");
        }

        // Recomendación: DESACTIVAR ESTA CLASE (removiendo @Component) después de la primera ejecución exitosa.
    }
    
    // --- MÉTODOS AUXILIARES ---

    private byte[] descargarImagenDesdeUrl(String url) {
        // Usa RestTemplate para obtener los bytes del recurso
        return restTemplate.getForObject(URI.create(url), byte[].class);
    }

    private MultipartFile crearMultipartFileDesdeBytes(byte[] bytes, String fileName) {
        // Implementación de MultipartFile usando una clase anónima
        return new MultipartFile() {
            @Override
            public String getName() { return "file"; }
            @Override
            public String getOriginalFilename() { return fileName; }
            @Override
            public String getContentType() { return "application/octet-stream"; } // Placeholder
            @Override
            public boolean isEmpty() { return bytes == null || bytes.length == 0; }
            @Override
            public long getSize() { return bytes.length; }
            @Override
            public byte[] getBytes() throws IOException { return bytes; }
            @Override
            public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(bytes); }
            @Override
            public void transferTo(java.io.File dest) throws IOException, IllegalStateException { throw new UnsupportedOperationException("No implementado"); }
            @Override
            public void transferTo(java.nio.file.Path dest) throws IOException, IllegalStateException { throw new UnsupportedOperationException("No implementado"); }
        };
    }
}