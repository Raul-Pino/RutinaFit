package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioBuscarResponse;
import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.dto.UsuarioUpdateRequest;
import com.example.rutinafit.model.Solicitud;
import com.example.rutinafit.model.TipoSolicitud;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.SolicitudRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.util.FilesUtils;
import com.example.rutinafit.util.SecurityUtils;
import com.example.rutinafit.util.UsuarioMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Value("${app.url-back}")
    private String urlBack;
    
    @Value("${app.upload.dir}")
    private String uploadDir;

    private final SecurityUtils securityUtils;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;
    private final PasswordEncoder encoder;
    private final AmistadService amistadService;
    private final EmailService emailService;
    private final FilesUtils filesUtils;

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
    public List<UsuarioBuscarResponse> buscarUsuarios(Long id) {
        return usuarioRepository.findAll().stream()
                .filter(u -> !u.getRol().equals("ADMIN") && !u.getId().equals(id)) // No mostrar a los admins ni a uno
                                                                                   // mismo
                .map(u -> usuarioMapper.pasarABuscarDTO(u, amistadService.sonAmigos(id, u.getId())))
                .toList();
    }

    public UsuarioResponse buscarUsuarioPorId(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.pasarADTO(u);
    }

    // ELIMINAR (Admin elimina usuario o usuario se da de baja)
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        List<Usuario> alumnos = usuarioRepository.findByEntrenadorId(id);
        for (Usuario alumno : alumnos) {
            this.dejarEntrenador(alumno.getId(), id);
        }

        List<Usuario> entrenadores = usuarioRepository.findByEntrenadorId(id);
        for (Usuario entrenador : entrenadores) {
            this.dejarEntrenador(id, entrenador.getId());
        }

        List<UsuarioResponse> amigos = amistadService.listarMisAmigos(id);
        for (UsuarioResponse amigo : amigos) {
            amistadService.eliminarAmistad(amigo.id(), id);
        }

        List<Solicitud> solicitudes = solicitudRepository.findByRemitenteId(id);
        for (Solicitud solicitud : solicitudes) {
            solicitudRepository.delete(solicitud);
        }

        List<Solicitud> solicitudes2 = solicitudRepository.findByDestinatarioId(id);
        for (Solicitud solicitud : solicitudes2) {
            solicitudRepository.delete(solicitud);
        }

        usuarioRepository.deleteById(id);
    }

    // ACTUALIZAR
    public UsuarioResponse update(Long userId, UsuarioUpdateRequest dto, MultipartFile fotoPerfil) throws IOException {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.isEsEntrenador() && !dto.esEntrenador()) {
            // Si el usuario era entrenador y ahora no lo es, se eliminan las relaciones con
            // sus alumnos
            List<Usuario> alumnos = usuarioRepository.findByEntrenadorId(usuario.getId());
            for (Usuario alumno : alumnos) {
                this.dejarEntrenador(alumno.getId(), userId);
            }
        }

        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setEsEntrenador(dto.esEntrenador());

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            // Borrar la antigua solo si es un fichero local (no una URL externa)
            if (usuario.getFotoPerfil() != null && usuario.getFotoPerfil().startsWith(urlBack)) {
                String rutaAntigua = usuario.getFotoPerfil().replace(urlBack, "");
                Path antigua = Paths.get(uploadDir  + rutaAntigua);
                Files.deleteIfExists(antigua);
            }

            Path ruta = Paths.get(uploadDir + "/avatars");
            Files.createDirectories(ruta);
            String filename = "avatar_" + userId + "_" + System.currentTimeMillis()
                            + filesUtils.getExtension(fotoPerfil.getOriginalFilename());
            fotoPerfil.transferTo(ruta.resolve(filename));
            usuario.setFotoPerfil(urlBack + "/avatars/" + filename);
        }

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

        if (alumno == null)
            return;

        Usuario entrenador = alumno.getEntrenador();
        if (entrenador == null) {
            throw new RuntimeException("Este usuario no tiene un entrenador asignado");
        }

        // Si no es el alumno ni el entrenador salta un error
        if (!alumno.getId().equals(solicitanteId) && !entrenador.getId().equals(solicitanteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para romper esta relación");
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

    @Transactional
    public void recuperarPassword(Map<String, String> datos) {
        String token = datos.get("token");
        String password = datos.get("password");
        String passwordConfirmacion = datos.get("passwordConfirmacion");

        if (password.isBlank() || token.isBlank() || passwordConfirmacion.isBlank()) {
            throw new RuntimeException("Faltan datos obligatorios");
        }

        if (!securityUtils.validarPassword(password)) {
            throw new RuntimeException("La contraseña no cumple con los requisitos");
        }

        if (!password.equals(passwordConfirmacion)) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no econtrado"));

        if (!usuario.isTokenValido()) {
            throw new RuntimeException("El token es invalido o ha expirado");
        }

        usuario.setPassword(encoder.encode(password));
        usuario.limpiarToken();
        usuarioRepository.save(usuario);
    }

    public void generarToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no econtrado"));

        if (usuario.isTokenValido()) {
            throw new RuntimeException("Ya existe un token activo. Por favor, espera 30 minutos.");
        }

        String token = UUID.randomUUID().toString();
        usuario.setToken(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusMinutes(30));
        usuarioRepository.save(usuario);

        emailService.enviarRecuperarPassword(email, token);
    }

    public boolean verificarToken(String token) {
        Usuario usuario = usuarioRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
        return usuario.isTokenValido();
    }

    public String getPropietario(Long alumnoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        securityUtils.validarAcceso(usuario, usuarioId);

        return usuario.getUsername();
    }
}