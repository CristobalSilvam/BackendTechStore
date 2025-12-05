package com.example.backendtech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backendtech.model.EstadoPedido;
import com.example.backendtech.model.ItemPedido;
import com.example.backendtech.model.Pedido;
import com.example.backendtech.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoService productoService;

    // Método para guardar el pedido que viene del Frontend
    public Pedido crearPedido(Pedido pedido, String emailUsuario) {
        
        // 1. Verificar y Descontar Stock
        boolean stockSuficiente = productoService.descontarStock(pedido);
        
        if (!stockSuficiente) {
            // Si el stock falla, podemos lanzar una excepción
            throw new RuntimeException("Stock insuficiente para completar el pedido.");
        }
        
        // 2. Asignar el usuario y el estado
        pedido.setEmailUsuario(emailUsuario);
        pedido.setEstado(EstadoPedido.ENVIADO); // Cambiamos el estado de PENDIENTE a ENVIADO/COMPLETADO
        
        // 3. Conectar cada ítem con el pedido principal
        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                item.setPedido(pedido);
            }
        }
        
        // 4. Guardar en la base de datos
        return pedidoRepository.save(pedido);
    }
    
    // Método para ver el historial de pedidos de un usuario
    public List<Pedido> obtenerPedidosPorUsuario(String emailUsuario) {
        return pedidoRepository.findByEmailUsuarioOrderByFechaCreacionDesc(emailUsuario);
    }
}