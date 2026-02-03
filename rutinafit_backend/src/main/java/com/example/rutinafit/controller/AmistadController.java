package com.example.rutinafit.controller;

import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.service.AmistadService;
import com.example.rutinafit.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/amistades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AmistadController {

    private final AmistadService amistadService;
    private final JwtService jwtService;

    /**
     * Lista todos los amigos del usuario logueado.
     */
    @GetMapping
    public ResponseEntity<List<Amistad>> listarMisAmigos(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtService.obetenerId(authHeader.substring(7));
        return ResponseEntity.ok(amistadService.listarAmigos(usuarioId));
    }

    /**
     * Elimina una relación de amistad por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAmistad(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = jwtService.obetenerId(authHeader.substring(7));
        amistadService.eliminarAmistad(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}