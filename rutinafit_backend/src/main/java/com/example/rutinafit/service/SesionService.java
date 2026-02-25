package com.example.rutinafit.service;

import com.example.rutinafit.dto.SesionRequest;
import com.example.rutinafit.dto.SesionResponse;
import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.SesionRepository;
import com.example.rutinafit.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final RutinaRepository rutinaRepository;
    private final SecurityUtils securityUtils;


    /**
     * Devuelve las sesiones de una rutina, PERO verifica que la rutina sea del usuario.
     */
    public List<SesionResponse> findByRutinaId(Long usuarioId, Long rutinaId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        return sesionRepository.findByRutinaId(rutinaId).stream()
                    .map( s -> new SesionResponse(s.getId(), s.getFecha(), rutinaId)).toList();
    }

    /**
     * Crea una sesión vinculándola a una rutina y verificando al usuario.
     */
    public SesionResponse create(Long usuarioId, Long rutinaId, SesionRequest dto) {
        // Buscamos la rutina padre
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        // Asignar los valores a la nueva sesión
        Sesion nuevaSesion = new Sesion();
        nuevaSesion.setFecha(dto.fecha());
        nuevaSesion.setRutina(rutina);

        // Guardar
        nuevaSesion = sesionRepository.save(nuevaSesion);
        return new SesionResponse(nuevaSesion.getId(), nuevaSesion.getFecha(), rutinaId);
    }

    /**
     * Elimina una sesión.
     */
    public void delete(Long usuarioId, Long sesionId) {
        // Buscamos la sesión
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(sesion.getRutina().getUsuario(), usuarioId);

        sesionRepository.deleteById(sesionId);
    }
}