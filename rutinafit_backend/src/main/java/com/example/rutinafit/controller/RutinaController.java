package com.example.rutinafit.controller;

import com.example.rutinafit.dto.RutinaRequest;
import com.example.rutinafit.dto.RutinaResponse;
import com.example.rutinafit.service.RutinaService;
import com.example.rutinafit.service.UsuarioService;
import com.example.rutinafit.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rutinas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RutinaController {

    private final UsuarioService usuarioService;
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
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RutinaResponse> crearRutina(
            @RequestPart("rutina") RutinaRequest rutina,
            @RequestPart(value = "fotoRutina", required = false) MultipartFile fotoRutina,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.create(usuarioId, usuarioId, rutina, fotoRutina));
    }

    /**
     * Actualizar una rutina de mi propiedad o de un alumno que entreno
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RutinaResponse> update(
            @PathVariable Long id,
            @Valid @RequestPart("rutina") RutinaRequest rutina,
            @RequestPart(value = "fotoRutina", required = false) MultipartFile fotoRutina,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.update(id, usuarioId, rutina, fotoRutina));
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
    @PostMapping(value = "/alumnos/{alumnoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RutinaResponse> crearRutinaAlumno(
            @PathVariable Long alumnoId,
            @RequestPart("rutina") RutinaRequest rutina,
            @RequestPart(value = "fotoRutina", required = false) MultipartFile fotoRutina,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaService.create(usuarioId, alumnoId, rutina, fotoRutina));
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

    /**
     * Devuelve el nombre del propietario de la rutina
     */
    @GetMapping("/{alumnoId}/propietario")
    public ResponseEntity<?> verPropietario(
            @PathVariable Long alumnoId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(Map.of("propietario", usuarioService.getPropietario(alumnoId, usuarioId)));
    }
}