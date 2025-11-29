package com.example.backendtech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backendtech.model.ItemPedido;
import com.example.backendtech.model.Pedido;
import com.example.backendtech.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Método para guardar el pedido que viene del Frontend (en el checkout)
    public Pedido crearPedido(Pedido pedido, String emailUsuario) {
        // 1. Asignar el usuario logueado (sacado del token)
        pedido.setEmailUsuario(emailUsuario);
        
        // 2. Conectar cada ítem de vuelta al pedido principal
        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                item.setPedido(pedido);
            }
        }
        
        // 3. Guardar en la base de datos
        return pedidoRepository.save(pedido);
    }
    
    // Método para ver el historial de pedidos de un usuario
    public List<Pedido> obtenerPedidosPorUsuario(String emailUsuario) {
        return pedidoRepository.findByEmailUsuarioOrderByFechaCreacionDesc(emailUsuario);
    }
}