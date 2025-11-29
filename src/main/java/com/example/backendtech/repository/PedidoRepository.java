package com.example.backendtech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendtech.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Para que el usuario vea su historial de pedidos
    List<Pedido> findByEmailUsuarioOrderByFechaCreacionDesc(String emailUsuario);
}