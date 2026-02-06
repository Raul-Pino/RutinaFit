package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioUpdateRequest;
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
    
    // ACTUALIZAR
    public Usuario update(Long id, UsuarioUpdateRequest dto) {
        Usuario usuarioExistente = findById(id);

        // Solo se actualiza si el DTO tiene información
        if (dto.username() != null && !dto.username().isBlank()) {
            usuarioExistente.setUsername(dto.username());
        }
        
        if (dto.email() != null && !dto.email().isBlank()) {
            usuarioExistente.setEmail(dto.email());
        }

        if (dto.esEntrenador() != null) {
            usuarioExistente.setEsEntrenador(dto.esEntrenador());
        }
        
        return usuarioRepository.save(usuarioExistente);
    }

    public List<Usuario> listarMisAlumnos(Long entrenadorId) {
        Usuario entrenador = findById(entrenadorId);
        if (!entrenador.isEsEntrenador()) {
            throw new RuntimeException("Solo los entrenadores pueden consultar su lista de alumnos.");
        }

        return usuarioRepository.findByEntrenadorId(entrenadorId);
    }

    @Transactional
    public void dejarEntrenador(Long alumnoId, Long solicitanteId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Usuario entrenador = alumno.getEntrenador();
        if (entrenador == null) {
            throw new RuntimeException("Este usuario no tiene un entrenador asignado");
        }

        // Si no es el alumno y ni el entrenador salta un error
        if (!alumno.getId().equals(solicitanteId) && !entrenador.getId().equals(solicitanteId)) {
                throw new RuntimeException("No tienes permiso para romper esta relación");
        }

        solicitudRepository.borrarSolicitudEntrenamiento(entrenador.getId(), alumno.getId());

        alumno.setEntrenador(null);
        usuarioRepository.save(alumno);
    }
}