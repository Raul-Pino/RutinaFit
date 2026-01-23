package com.example.rutinafit.service;

import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // LISTAR (Solo admins deberían poder hacer esto habitualmente)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // VER PERFIL
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ELIMINAR (Admin elimina usuario o usuario se da de baja)
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    
    // ACTUALIZAR (Este sí deberías añadirlo)
    // Sirve para cambiar nombre, email, peso, etc.
    public Usuario update(Long id, Usuario usuarioDatosNuevos) {
        Usuario usuarioExistente = findById(id);
        
        // Actualizamos solo los datos permitidos
        usuarioExistente.setUsername(usuarioDatosNuevos.getUsername());
        usuarioExistente.setEmail(usuarioDatosNuevos.getEmail());
        // NO actualizamos contraseña ni rol aquí (eso va en métodos separados por seguridad)
        
        return usuarioRepository.save(usuarioExistente);
    }
}