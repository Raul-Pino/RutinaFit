package com.example.rutinafit.controller;

import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.dto.UsuarioUpdateRequest;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.service.JwtService;
import com.example.rutinafit.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    // ===============
    // PARTE PÚBLICA
    // ===============

    /**
     * Ver mi propio perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> verMiPerfil(@RequestHeader("Authorization") String authHeader) {
        Long myId = getUsuarioId(authHeader);
        return ResponseEntity.ok(usuarioService.findById(myId));
    }

    /**
     * Eliminar perfil propio
     */
    @DeleteMapping("/perfil")
    public ResponseEntity<?> eliminarPerfil(@RequestHeader("Authorization") String authHeader) {
        Long myId = getUsuarioId(authHeader);
        usuarioService.delete(myId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar datos de perfil
     */
    @PutMapping("/perfil")
    public ResponseEntity<Usuario> actualizarMiPerfil(
            @RequestBody UsuarioUpdateRequest dto,
            @RequestHeader("Authorization") String authHeader){
        
        Long myId = getUsuarioId(authHeader);
        return ResponseEntity.ok(usuarioService.update(myId, dto));
    }

    /*
    * Buscar usuario por nombre
    */
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponse>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarUsuarios(nombre));
    }

    /*
    * Listar los alumnos del entrenador
    */
    @GetMapping("/alumnos")
    public ResponseEntity<?> getMisAlumnos(@RequestHeader("Authorization") String authHeader) {
        Long entrenadorId = getUsuarioId(authHeader);
        List<UsuarioResponse> alumnos = usuarioService.listarMisAlumnos(entrenadorId);
        return ResponseEntity.ok(alumnos);
    }

    /*
    * Eliminar entrenador
    */
    @PostMapping("/entrenador/quitar/{alumnoId}")
    public ResponseEntity<?> quitarEntrenador(@PathVariable Long alumnoId, @RequestHeader("Authorization") String authHeader) {
        Long solicitanteId = jwtService.obtenerId(authHeader.substring(7));
        usuarioService.dejarEntrenador(alumnoId, solicitanteId);
        return ResponseEntity.ok("Relación de entrenamiento finalizada");
    }

    // ===============
    // PARTE PRIVADA
    // ===============

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public ResponseEntity<?> listarTodos(@RequestHeader("Authorization") String authHeader) {
        // 1. Verificación de Rol
        if (!esAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Solo administradores.");
        }
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Eliminar un usuario por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable Long id, 
            @RequestHeader("Authorization") String authHeader) {
        
        if (!esAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Solo administradores.");
        }

        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===============
    // AUX
    // ===============

    private Long getUsuarioId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }
        String token = authHeader.substring(7);
        return jwtService.obtenerId(token);
    }

    private boolean esAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        return jwtService.esAdmin(token);
    }
}