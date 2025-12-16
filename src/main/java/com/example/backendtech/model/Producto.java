package com.example.backendtech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor; // Importa Data para getters, setters, equals, hashCode y toString
import lombok.Data; // Importa NoArgsConstructor
import lombok.NoArgsConstructor; // Importa AllArgsConstructor
import jakarta.persistence.Lob;

@Entity
@Table(name = "productos")
@Data // Genera automáticamente getters, setters, equals, hashCode, y toString
@NoArgsConstructor // Genera el constructor sin argumentos
@AllArgsConstructor // Genera el constructor con todos los argumentos
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private String descripcion;
    
    // ESTE CAMPO ES DONDE SE GUARDA EL NOMBRE DE ARCHIVO ÚNICO
    private String imagen; 
    
    private String categoria;
    private Integer stock;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String especificaciones;
}