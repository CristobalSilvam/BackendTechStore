package com.example.backendtech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backendtech.model.Usuario;
import com.example.backendtech.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Esta herramienta encripta las contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) {
        // 1. Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // 2. Si no viene rol, le asignamos USER por defecto
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER"); 
        }
        
        return usuarioRepository.save(usuario);
    }
    
    // Método para buscar por email
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
}