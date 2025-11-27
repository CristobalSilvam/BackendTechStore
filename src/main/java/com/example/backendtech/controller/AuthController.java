package com.example.backendtech.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendtech.model.Usuario;
import com.example.backendtech.service.JwtService;
import com.example.backendtech.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager; // El que verifica el password

    @PostMapping("/register")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    // ENDPOINT: LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario usuario) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Generamos Token
            String token = jwtService.generateToken(usuario.getEmail());
            
            // Buscamos al usuario completo para saber su rol
            Usuario usuarioEncontrado = usuarioService.buscarPorEmail(usuario.getEmail());

            // Respondemos con un JSON { "token": "...", "role": "ADMIN" }
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("role", usuarioEncontrado.getRole()); 
            return respuesta;

        } else {
            throw new UsernameNotFoundException("Usuario inválido");
        }
    }
}