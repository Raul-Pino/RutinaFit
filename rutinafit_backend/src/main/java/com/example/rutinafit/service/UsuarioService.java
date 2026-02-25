package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.dto.UsuarioUpdateRequest;
import com.example.rutinafit.model.TipoSolicitud;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.SolicitudRepository;
import com.example.rutinafit.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;

    // LISTAR (Solo admins deberían poder hacer esto habitualmente)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(this::transformarADTO).toList();
    }

    // VER PERFIL
    public UsuarioResponse findById(Long id) {
        Usuario u = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return transformarADTO(u);
    }

    // Buscar por Nombre de usuario
    public List<UsuarioResponse> buscarUsuarios(String nombre) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(nombre)
                .stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol()))
                .toList();
    }

    // ELIMINAR (Admin elimina usuario o usuario se da de baja)
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    
    // ACTUALIZAR
    public UsuarioResponse update(Long userId, UsuarioUpdateRequest dto) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setEsEntrenador(dto.esEntrenador());
        
        usuarioRepository.save(usuario);
        return transformarADTO(usuario);
    }

    // Listar Alumnos de un enternador
    public List<UsuarioResponse> listarMisAlumnos(Long entrenadorId) {
        Usuario entrenador = usuarioRepository.findById(entrenadorId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    
        if (!entrenador.isEsEntrenador()) {
            throw new RuntimeException("Solo los entrenadores pueden consultar su lista de alumnos.");
        }

        return usuarioRepository.findByEntrenadorId(entrenadorId).stream().map(this::transformarADTO).toList();
    }

    // Dejar de ser Entrenador o dejar de ser Alumno de un entrenador
    @Transactional
    public void dejarEntrenador(Long alumnoId, Long solicitanteId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Usuario entrenador = alumno.getEntrenador();
        if (entrenador == null) {
            throw new RuntimeException("Este usuario no tiene un entrenador asignado");
        }

        // Si no es el alumno y ni el entrenador salta un error
        if (alumno.getId() != solicitanteId && entrenador.getId() != solicitanteId) {
                throw new RuntimeException("No tienes permiso para romper esta relación");
        }

        solicitudRepository.borrarSolicitud(entrenador.getId(), alumno.getId(), TipoSolicitud.ENTRENAMIENTO);

        alumno.setEntrenador(null);
        usuarioRepository.save(alumno);
    }

    // "Transforma" un objeto usuario a un objeto que puede enviarse al frontEnd (Eliminando información sensible)
    public UsuarioResponse transformarADTO(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail(), usuario.getRol());
    }
}