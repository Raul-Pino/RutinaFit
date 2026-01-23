package com.example.rutinafit.controller;

import com.example.rutinafit.model.EjercicioInfo;
import com.example.rutinafit.service.EjercicioInfoService;
import com.example.rutinafit.service.JwtService;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ejercicios-info")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EjercicioInfoController {

    private final EjercicioInfoService ejercicioInfoService;
    private final JwtService jwtService;

    // ===============
    // PARTE PÚBLICA
    // ===============

    /**
     * Listar todos los tipos de ejercicios
     */
    @GetMapping
    public ResponseEntity<List<EjercicioInfo>> findAll() {
        return ResponseEntity.ok(ejercicioInfoService.findAll());
    }
    
    /**
     * Lista todos los ejercicios de un tipo específico
     */
    @GetMapping("/tipo/{codigo}")
    public ResponseEntity<List<EjercicioInfo>> findByCodigo(@PathVariable int codigo) {
        return ResponseEntity.ok(ejercicioInfoService.findByCodigo(codigo));
    }

    // ===============
    // PARTE Privada
    // ===============

    /**
     * Crear un nuevo ejercicio en el catálogo
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody EjercicioInfo info,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);

        if (!jwtService.esAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Se requiere rol de Administrador");
        }

        return ResponseEntity.ok(ejercicioInfoService.create(info));
    }

    /**
     * Actualizar un ejercicio del catálogo
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody EjercicioInfo info,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        if (!jwtService.esAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Solo administradores.");
        }

        return ResponseEntity.ok(ejercicioInfoService.update(id, info));
    }
    

    /**
     * Borrar un ejercicio del catálogo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        if (!jwtService.esAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Se requiere rol de Administrador");
        }

        ejercicioInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}