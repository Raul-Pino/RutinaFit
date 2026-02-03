package com.example.rutinafit.controller;

import com.example.rutinafit.model.Solicitud;
import com.example.rutinafit.service.JwtService;
import com.example.rutinafit.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final JwtService jwtService;

    @PostMapping("/enviar/{destinatarioId}")
    public ResponseEntity<Solicitud> enviar(
            @PathVariable Long destinatarioId,
            @RequestParam String tipo,
            @RequestHeader("Authorization") String authHeader) {
        
        Long remitenteId = jwtService.obetenerId(authHeader.substring(7));
        return ResponseEntity.ok(solicitudService.enviarSolicitud(remitenteId, destinatarioId, tipo));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Solicitud>> listar(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtService.obetenerId(authHeader.substring(7));
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesPendientes(usuarioId));
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = jwtService.obetenerId(authHeader.substring(7));
        solicitudService.aceptarSolicitud(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
        @PathVariable Long id,
        @RequestHeader("Authorization") String authHeader) {
    
    Long usuarioId = jwtService.obetenerId(authHeader.substring(7));
    solicitudService.rechazarSolicitud(id, usuarioId);
    return ResponseEntity.ok().build();
}
}