package com.example.backendtech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendtech.model.Pedido;
import com.example.backendtech.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // ENDPOINT PARA CREAR UN PEDIDO (Checkout)
    // Protegido: Solo usuarios autenticados pueden crear pedidos.
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido, Authentication authentication) {
        String emailUsuario = authentication.getName(); 
        
        Pedido nuevoPedido = pedidoService.crearPedido(pedido, emailUsuario);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    // ENDPOINT PARA VER EL HISTORIAL DE PEDIDOS DEL USUARIO
    // Protegido: Solo usuarios autenticados pueden ver su historial.
    @GetMapping("/historial")
    public List<Pedido> obtenerHistorial(Authentication authentication) {
        String emailUsuario = authentication.getName();
        return pedidoService.obtenerPedidosPorUsuario(emailUsuario);
    }
}