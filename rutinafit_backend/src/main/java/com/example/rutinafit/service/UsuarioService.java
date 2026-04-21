package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.dto.UsuarioUpdateRequest;
import com.example.rutinafit.model.TipoSolicitud;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.SolicitudRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.util.SecurityUtils;
import com.example.rutinafit.util.UsuarioMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final SecurityUtils securityUtils;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;
    private final PasswordEncoder encoder;

    // LISTAR (Solo admins deberían poder hacer esto habitualmente)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(u -> usuarioMapper.pasarADTO(u)).toList();
    }

    // VER PERFIL
    public UsuarioResponse findById(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.pasarADTO(u);
    }

    // Buscar por Nombre de usuario
    public List<UsuarioResponse> buscarUsuarios(String nombre) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(nombre)
                .stream()
                .map(u -> usuarioMapper.pasarADTO(u))
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
        return usuarioMapper.pasarADTO(usuario);
    }

    // Listar Alumnos de un enternador
    public List<UsuarioResponse> listarMisAlumnos(Long entrenadorId) {
        Usuario entrenador = usuarioRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!entrenador.isEsEntrenador()) {
            throw new RuntimeException("Solo los entrenadores pueden consultar su lista de alumnos.");
        }

        return usuarioRepository.findByEntrenadorId(entrenadorId).stream().map(u -> usuarioMapper.pasarADTO(u))
                .toList();
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

    // Cambiar contraseña, se debe introducir la contraseña actual y la nueva
    public void cambiarPassword(Long id, Map<String, String> datos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String actual = datos.get("passwordActual");
        String nueva = datos.get("passwordNueva");

        if (actual.isBlank() || nueva.isBlank()) {
            throw new RuntimeException("Faltan datos obligatorios");
        }

        if (!securityUtils.validarPassword(nueva)) {
            throw new RuntimeException("La contraseña no cumple con los requisitos");
        }

        if (!encoder.matches(actual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña es incorrecta");
        }

        usuario.setPassword(encoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    // Recuperar contraseña, se debe introducir el email y la nueva contraseña
    public void recuperarPassword(Map<String, String> datos) {
        String email = datos.get("email");
        String password = datos.get("password");
        String passwordConfirmacion = datos.get("passwordConfirmacion");

        if (password.isBlank() || email.isBlank() || passwordConfirmacion.isBlank()) {
            throw new RuntimeException("Faltan datos obligatorios");
        }

        if (!securityUtils.validarPassword(password)) {
            throw new RuntimeException("La contraseña no cumple con los requisitos");
        }

        if (!password.equals(passwordConfirmacion)) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no econtrado"));

        usuario.setPassword(encoder.encode(password));
        usuarioRepository.save(usuario);
    }
}