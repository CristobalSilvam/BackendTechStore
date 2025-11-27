package com.example.backendtech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendtech.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
}