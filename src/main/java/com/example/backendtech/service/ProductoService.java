package com.example.backendtech.service;

import com.example.backendtech.model.Producto;
import com.example.backendtech.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Importante: Esto le dice a Spring "Aquí hay lógica de negocio"
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        // AQUÍ es donde pondrías validaciones antes de guardar.
        // Ejemplo: if (producto.getPrecio() < 0) throw new Error...
        return productoRepository.save(producto);
    }
    
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
}