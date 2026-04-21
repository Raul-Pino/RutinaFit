package com.example.rutinafit.controller;

import com.example.rutinafit.dto.EjercicioRequest;
import com.example.rutinafit.dto.EjercicioResponse;
import com.example.rutinafit.service.EjercicioService;
import com.example.rutinafit.service.UsuarioService;
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
import java.util.Map;

@RestController
@RequestMapping("/ejercicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EjercicioController {

    private final UsuarioService usuarioService;
    private final EjercicioService ejercicioService;
    private final SecurityUtils securityUtils;


    /**
     * Añadir una ejercicio (serie) a una sesión
     * ejemplo : 
     * { "ejercicioInfoId": 5, "param1": 50.0, "param2": 10.0 }
     */
    @PostMapping("/sesion/{sesionId}")
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @PathVariable Long sesionId,
            @RequestBody EjercicioRequest requestDTO,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(ejercicioService.create(usuarioId, sesionId, requestDTO));
    }

    /**
     * Actualizar un ejercicio por su ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest requestDTO, 
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(ejercicioService.update(usuarioId, id, requestDTO));
    }

    /**
     * Listar todos los ejercicios de una sesión
     */
    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<List<EjercicioResponse>> listarEjercicios(
            @PathVariable Long sesionId,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(ejercicioService.findBySesionId(usuarioId, sesionId));
    }

    /**
     * Eliminar un ejercicio por su ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEjercicio(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        ejercicioService.delete(usuarioId, id);
        return ResponseEntity.noContent().build();
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