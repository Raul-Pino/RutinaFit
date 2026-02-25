package com.example.rutinafit.controller;

import com.example.rutinafit.dto.RutinaRequest;
import com.example.rutinafit.dto.RutinaResponse;
import com.example.rutinafit.service.RutinaService;
import com.example.rutinafit.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rutinas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RutinaController {

    private final RutinaService rutinaService;
    private final SecurityUtils securityUtils;

    /**
     * Listar mis rutinas
     */
    @GetMapping
    public ResponseEntity<List<RutinaResponse>> getMisRutinas(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.findByUsuarioId(usuarioId));
    }

    /**
     * Ver una rutina de mi propiedad
     */
    @GetMapping("/{id}")
    public ResponseEntity<RutinaResponse> getRutina(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.findByIdAndUsuarioId(id, usuarioId));
    }

    /**
     * Crear una nueva rutina de mi propiedad
     */
    @PostMapping
    public ResponseEntity<RutinaResponse> crearRutina(
            @RequestBody RutinaRequest rutina,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.create(usuarioId, usuarioId, rutina));
    }

    /**
     * Actualizar una rutina de mi propiedad
     */
    @PutMapping("/{id}")
    public ResponseEntity<RutinaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RutinaRequest rutina,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.update(id, usuarioId, rutina));
    }

    /**
     * Eliminar una rutina de mi propiedad
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        rutinaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }


    // ===============
    // PARTE ENTRENADOR
    // ===============

    /**
     * Crear una nueva rutina para un alumno
     */
    @PostMapping("/alumnos/{alumnoId}")
    public ResponseEntity<RutinaResponse> crearRutinaAlumno(
            @PathVariable Long alumnoId,
            @RequestBody RutinaRequest rutina,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.create(usuarioId, alumnoId, rutina));
    }

    /**
     * Listar rutinas de alumnos
     */
    @GetMapping("/alumnos/{alumnoId}")
    public ResponseEntity<List<RutinaResponse>> ListarRutinasAlumno(
            @PathVariable Long alumnoId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.findByUsuarioId(usuarioId, alumnoId));
    }
}