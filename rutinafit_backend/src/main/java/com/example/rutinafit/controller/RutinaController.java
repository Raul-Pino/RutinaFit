package com.example.rutinafit.controller;

import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.service.JwtService;
import com.example.rutinafit.service.RutinaService;

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
    private final JwtService jwtService;

    /**
     * Listar mis rutinas
     */
    @GetMapping
    public ResponseEntity<List<Rutina>> getMisRutinas(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = getUsuarioIdFromHeader(authHeader);
        return ResponseEntity.ok(rutinaService.findByUsuarioId(usuarioId));
    }

    /**
     * Ver una rutina de mi propiedad
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rutina> getRutina(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = getUsuarioIdFromHeader(authHeader);
        return ResponseEntity.ok(rutinaService.findByIdAndUsuarioId(id, usuarioId));
    }

    /**
     * Crear una nueva rutina de mi propiedad
     */
    @PostMapping
    public ResponseEntity<Rutina> create(
            @RequestBody Rutina rutina,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioIdFromHeader(authHeader);
        return ResponseEntity.ok(rutinaService.create(usuarioId, rutina));
    }

    /**
     * Actualizar una rutina de mi propiedad
     */
    @PutMapping("/{id}")
    public ResponseEntity<Rutina> update(
            @PathVariable Long id,
            @Valid @RequestBody Rutina rutinaDatos,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioIdFromHeader(authHeader);
        
        return ResponseEntity.ok(rutinaService.update(id, usuarioId, rutinaDatos));
    }

    /**
     * Eliminar una rutina de mi propiedad
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioIdFromHeader(authHeader);
        rutinaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ===============
    // AUX
    // ===============

    private Long getUsuarioIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o inexistente");
        }
        String token = authHeader.substring(7);
        return jwtService.obetenerId(token);
    }
}