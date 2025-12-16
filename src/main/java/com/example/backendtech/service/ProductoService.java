package com.example.backendtech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendtech.exception.ResourceNotFoundException;
import com.example.backendtech.model.ItemPedido;
import com.example.backendtech.model.Pedido;
import com.example.backendtech.model.Producto;
import com.example.backendtech.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service 
@Transactional // Recomendado para asegurar la consistencia en las operaciones de DB
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FileStorageService fileStorageService; // <-- INYECCIÓN DEL SERVICIO DE ARCHIVOS

    // Inyección por constructor
    public ProductoService(ProductoRepository productoRepository, FileStorageService fileStorageService) {
        this.productoRepository = productoRepository;
        this.fileStorageService = fileStorageService;
    }

    // Método de utilidad para obtener producto o lanzar 404
    public Producto getProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }


    // 1. MÉTODO DE CREACIÓN (Con soporte para imagen)
    public Producto guardarProducto(
            String nombre, 
            Double precio, 
            String descripcion, 
            MultipartFile imagen, 
            String categoria, 
            Integer stock,
            String especificaciones) {
        
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setStock(stock);
        producto.setEspecificaciones(especificaciones); 

        // Lógica de archivo: Si se envió una imagen, la guardamos
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen); // Guarda en disco
            producto.setImagen(fileName); // Guarda el nombre único en la DB
        }
        
        return productoRepository.save(producto);
    }
    
    
    // 2. MÉTODO DE ACTUALIZACIÓN (Con soporte para imagen)
    public Producto actualizarProducto(
            Long id, 
            Producto productoDetalles, // Podrías usar un DTO aquí
            MultipartFile nuevaImagen) { 

        Producto productoExistente = getProductoById(id);

        // Actualización de campos normales
        productoExistente.setNombre(productoDetalles.getNombre());
        productoExistente.setPrecio(productoDetalles.getPrecio());
        productoExistente.setDescripcion(productoDetalles.getDescripcion());
        productoExistente.setCategoria(productoDetalles.getCategoria());
        productoExistente.setEspecificaciones(productoDetalles.getEspecificaciones());
        
        if (productoDetalles.getStock() != null) {
            productoExistente.setStock(productoDetalles.getStock());
        }

        // Lógica de archivo: Manejo de la nueva imagen
        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            // A. Si ya tenía una imagen, la eliminamos del disco
            if (productoExistente.getImagen() != null && !productoExistente.getImagen().isEmpty()) {
                fileStorageService.deleteFile(productoExistente.getImagen());
            }
            // B. Guardamos la nueva imagen
            String fileName = fileStorageService.storeFile(nuevaImagen); 
            productoExistente.setImagen(fileName); // Guardamos el nuevo nombre en la DB
        }
        
        return productoRepository.save(productoExistente);
    }
    
    
    // 3. MÉTODO DE ELIMINACIÓN (Elimina el registro y el archivo)
    public void eliminarProducto(Long id) {
        Producto productoExistente = getProductoById(id);

        // Lógica de archivo: Eliminar la imagen del disco
        if (productoExistente.getImagen() != null && !productoExistente.getImagen().isEmpty()) {
            fileStorageService.deleteFile(productoExistente.getImagen());
        }

        productoRepository.delete(productoExistente);
    }
    
    
    // --- MÉTODOS EXISTENTES / AUXILIARES ---

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    // --- MANTENEMOS EL MÉTODO DE STOCK ORIGINAL ---
    public boolean descontarStock(Pedido pedido) {
         for (ItemPedido item : pedido.getItems()) {
             // Usamos la excepción personalizada en lugar de Optional.isEmpty
             Producto producto = getProductoById(item.getProductoId());
             
             int cantidadComprada = item.getCantidad();
             
             if (producto.getStock() < cantidadComprada) {
                 return false; 
             }
             
             producto.setStock(producto.getStock() - cantidadComprada);
             productoRepository.save(producto);
         }
         return true;
    }
}