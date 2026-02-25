package com.example.rutinafit.controller;

import com.example.rutinafit.dto.SesionRequest;
import com.example.rutinafit.dto.SesionResponse;
import com.example.rutinafit.service.SesionService;
import com.example.rutinafit.util.SecurityUtils;

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
    private final SecurityUtils securityUtils;

    /**
     * Crear una nueva sesión en una rutina
     */
    @PostMapping("/rutinas/{rutinaId}/sesiones")
    public ResponseEntity<SesionResponse> crearSesion(
            @PathVariable Long rutinaId,
            @RequestBody SesionRequest sesion,
            @RequestHeader("Authorization") String authHeader) {

        Long usuarioId = securityUtils.getUsuarioId(authHeader);        
        return ResponseEntity.ok(sesionService.create(usuarioId, rutinaId, sesion));
    }

    /**
     * Listar todas las sesiones de una rutina
     */
    @GetMapping("/rutinas/{rutinaId}/sesiones")
    public ResponseEntity<List<SesionResponse>> listarSesiones(
            @PathVariable Long rutinaId,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(sesionService.findByRutinaId(usuarioId, rutinaId));
    }

    /**
     * Eliminar una sesión por su ID
     */
    @DeleteMapping("/sesiones/{id}")
    public ResponseEntity<?> eliminarSesion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        sesionService.delete(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

}