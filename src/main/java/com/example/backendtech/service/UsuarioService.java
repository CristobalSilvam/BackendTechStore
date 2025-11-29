package com.example.backendtech.service;

import java.util.List;

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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Método existente para REGISTRO (POST)
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER"); 
        }
        return usuarioRepository.save(usuario);
    }
    
    // Método existente para LOGIN (GET)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // LISTAR TODOS LOS USUARIOS (GET)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // ELIMINAR USUARIO (DELETE)
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // EDITAR ROL/NOMBRE DE USUARIO (PUT)
    public Usuario actualizarUsuario(Long id, Usuario detallesUsuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo permitimos cambiar nombre y rol desde el panel admin
        usuarioExistente.setNombre(detallesUsuario.getNombre());
        usuarioExistente.setRole(detallesUsuario.getRole());
        
        return usuarioRepository.save(usuarioExistente);
    }
}