package com.example.backendtech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backendtech.model.ItemPedido;
import com.example.backendtech.model.Pedido;
import com.example.backendtech.model.Producto;
import com.example.backendtech.repository.ProductoRepository;

@Service 
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;


    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoExistente.setNombre(productoDetalles.getNombre());
        productoExistente.setPrecio(productoDetalles.getPrecio());
        productoExistente.setDescripcion(productoDetalles.getDescripcion());
        productoExistente.setImagen(productoDetalles.getImagen());
        productoExistente.setCategoria(productoDetalles.getCategoria());
        
        // --- El stock se actualiza aquí ---
        if (productoDetalles.getStock() != null) {
             productoExistente.setStock(productoDetalles.getStock());
        }
        
        return productoRepository.save(productoExistente);
    }

    // --- DESCUENTO DE STOCK AL VENDER ---
    public boolean descontarStock(Pedido pedido) {
        for (ItemPedido item : pedido.getItems()) {
            Optional<Producto> optProducto = productoRepository.findById(item.getProductoId());
            
            if (optProducto.isEmpty()) {
                // Si el producto no existe, no se puede completar el pedido
                return false; 
            }
            
            Producto producto = optProducto.get();
            int cantidadComprada = item.getCantidad();
            
            if (producto.getStock() < cantidadComprada) {
                // Si no hay stock suficiente, detenemos la compra
                return false; 
            }
            
            // Descontamos el stock
            producto.setStock(producto.getStock() - cantidadComprada);
            productoRepository.save(producto);
        }
        return true;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
}