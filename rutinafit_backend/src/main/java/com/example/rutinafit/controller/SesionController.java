package com.example.rutinafit.controller;

import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.service.JwtService;
import com.example.rutinafit.service.SesionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SesionController {

    private final SesionService sesionService;
    private final JwtService jwtService;

    /**
     * Crear una nueva sesión en una rutina
     */
    @PostMapping("/rutinas/{rutinaId}/sesiones")
    public ResponseEntity<Sesion> crearSesion(
            @PathVariable Long rutinaId,
            @RequestBody Sesion sesion,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioId(authHeader);
        
        return ResponseEntity.ok(sesionService.create(usuarioId, rutinaId, sesion));
    }

    /**
     * Listar todas las sesiones de una rutina
     */
    @GetMapping("/rutinas/{rutinaId}/sesiones")
    public ResponseEntity<List<Sesion>> listarSesiones(
            @PathVariable Long rutinaId,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioId(authHeader);
        return ResponseEntity.ok(sesionService.findByRutinaId(usuarioId, rutinaId));
    }

    /**
     * Eliminar una sesión por su ID
     */
    @DeleteMapping("/sesiones/{id}")
    public ResponseEntity<?> eliminarSesion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = getUsuarioId(authHeader);
        sesionService.delete(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // ===============
    // AUX
    // ===============

    private Long getUsuarioId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }
        return jwtService.obetenerId(authHeader.substring(7));
    }
}