package com.example.backendtech.controller;

import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendtech.model.Producto;
import com.example.backendtech.service.FileStorageService;
import com.example.backendtech.service.ProductoService;

// Importante para que React (u otros frontends) pueda consumir la API
@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final FileStorageService fileStorageService;

    public ProductoController(ProductoService productoService, FileStorageService fileStorageService) {
        this.productoService = productoService;
        this.fileStorageService = fileStorageService;
    }

    // Obtener todos
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        // Usamos el servicio de producto para obtener o lanzar 404
        Producto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }


    // 1. CREAR PRODUCTO (Acepta Multipart/Form-Data)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> crearProducto(
            // @RequestParam se usa para recibir campos de texto en un form-data
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "especificaciones", required = false) String especificaciones,
            // @RequestPart se usa para recibir archivos en un form-data
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        Producto created = productoService.guardarProducto(
                nombre, precio, descripcion, imagen, categoria, stock, especificaciones);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    
    // 2. ACTUALIZAR PRODUCTO (Acepta Multipart/Form-Data)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "especificaciones", required = false) String especificaciones,
            @RequestPart(value = "imagen", required = false) MultipartFile nuevaImagen) {

        // Creamos un objeto Producto con los detalles recibidos para pasarlo al servicio
        Producto detalles = new Producto();
        detalles.setNombre(nombre);
        detalles.setPrecio(precio);
        detalles.setDescripcion(descripcion);
        detalles.setCategoria(categoria);
        detalles.setStock(stock);
        detalles.setEspecificaciones(especificaciones);

        Producto updated = productoService.actualizarProducto(id, detalles, nuevaImagen);
        return ResponseEntity.ok(updated);
    }


    // 3. ELIMINAR PRODUCTO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }


    // 4. ENDPOINT PARA SERVIR IMÁGENES (GET /api/productos/images/nombre_archivo.jpg)
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        // 1. Cargar el archivo como Resource desde el disco
        Resource resource = fileStorageService.loadFileAsResource(filename);
        
        String contentType = "application/octet-stream";
        try {
            // Intentar determinar el tipo MIME (ej: image/png, image/jpeg)
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception e) {
            // Si falla, se queda con el genérico octet-stream
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}