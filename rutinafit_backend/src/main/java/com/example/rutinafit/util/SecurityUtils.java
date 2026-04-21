package com.example.rutinafit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.service.JwtService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Valida si existe el token o si es válido el token
     */

    public Long getUsuarioId(String authHeader) {
        // Comprobar que es un Token válido
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT no encontrado o formato inválido");
        }
        
        String token = authHeader.substring(7);
        
        // 3. Validar que no está caducado
        if (jwtService.validarYObtenerUsuario(token) == null || 
            usuarioRepository.findById(jwtService.obtenerId(token)).isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token es inválido o ha expirado");
        }

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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta rutina");
        }
    }


    public boolean validarPassword(String password){
        String regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
