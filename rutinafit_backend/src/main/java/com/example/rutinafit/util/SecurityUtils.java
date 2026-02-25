package com.example.rutinafit.util;

import org.springframework.stereotype.Component;

import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.service.JwtService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtService jwtService;

    /**
     * Valida si existe el token o si es válido el token
     */

    public Long getUsuarioId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT no encontrado o formato inválido");
        }
        String token = authHeader.substring(7);
        return jwtService.obtenerId(token);
    }


    /**
     * Valida si el solicitante es el dueño o su entrenador
     * Se usa para Rutinas, Sesiones y Ejercicios.
     */
    public void validarAcceso(Usuario propietario, Long solicitanteId) {
        boolean esProietario = propietario.getId() == solicitanteId;
        
        boolean esEntrenador = propietario.getEntrenador() != null && propietario.getEntrenador().getId() == solicitanteId;

        if (!esProietario && !esEntrenador) {
            throw new RuntimeException("Acceso denegado: No eres el propietario ni el entrenador asignado");
        }
    }
}
