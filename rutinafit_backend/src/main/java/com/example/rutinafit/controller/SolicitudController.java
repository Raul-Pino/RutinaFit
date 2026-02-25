package com.example.rutinafit.controller;

import com.example.rutinafit.dto.SolicitudRequest;
import com.example.rutinafit.dto.SolicitudResponse;
import com.example.rutinafit.service.SolicitudService;
import com.example.rutinafit.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SecurityUtils securityUtils;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(
        @Valid @RequestBody SolicitudRequest dto, 
        @RequestHeader("Authorization") String authHeader){
        Long remitenteId = securityUtils.getUsuarioId(authHeader);
        solicitudService.enviarSolicitud(remitenteId, dto);
        return ResponseEntity.ok("Solicitud procesada");
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudResponse>> listar(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesPendientes(usuarioId));
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = securityUtils.getUsuarioId(authHeader);
        solicitudService.aceptarSolicitud(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
        @PathVariable Long id,
        @RequestHeader("Authorization") String authHeader) {
    
    Long usuarioId = securityUtils.getUsuarioId(authHeader);
    solicitudService.rechazarSolicitud(id, usuarioId);
    return ResponseEntity.ok().build();
}
}