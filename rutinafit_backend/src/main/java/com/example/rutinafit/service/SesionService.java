package com.example.rutinafit.service;

import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final RutinaRepository rutinaRepository;

    /**
     * Devuelve las sesiones de una rutina, PERO verifica que la rutina sea del usuario.
     */
    public List<Sesion> findByRutinaId(Long usuarioId, Long rutinaId) {
        // 1. Buscamos la rutina
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // 2. SEGURIDAD: ¿Esta rutina es del usuario que la pide?
        if (!rutina.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para ver estas sesiones");
        }

        return sesionRepository.findByRutinaId(rutinaId);
    }

    /**
     * Crea una sesión vinculándola a una rutina y verificando al usuario.
     */
    public Sesion create(Long usuarioId, Long rutinaId, Sesion sesionDatos) {
        // 1. Buscamos la rutina padre
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // 2. Comprobarmos que el usuario es el propietario de la rutina
        if (!rutina.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No puedes añadir sesiones a una rutina que no es tuya");
        }

        // 3. Asignar la rutina a la sesion
        sesionDatos.setRutina(rutina);

        // 4. Guardar
        return sesionRepository.save(sesionDatos);
    }

    /**
     * Elimina una sesión verificando toda la cadena de propiedad.
     */
    public void delete(Long usuarioId, Long sesionId) {
        // 1. Buscamos la sesión
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Verificamos si la rutina de esta sesión pertenece al usuario
        if (!sesion.getRutina().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta sesión");
        }

        sesionRepository.deleteById(sesionId);
    }
}