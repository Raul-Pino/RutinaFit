package com.example.rutinafit.service;

import com.example.rutinafit.dto.SesionRequest;
import com.example.rutinafit.dto.SesionResponse;
import com.example.rutinafit.model.Ejercicio;
import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.repository.EjercicioRepository;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.SesionRepository;
import com.example.rutinafit.util.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final RutinaRepository rutinaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final SecurityUtils securityUtils;

    /**
     * Devuelve las sesiones de una rutina, PERO verifica que la rutina sea del
     * usuario.
     */
    public List<SesionResponse> findByRutinaId(Long usuarioId, Long rutinaId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        return sesionRepository.findByRutinaId(rutinaId).stream()
                .map(s -> new SesionResponse(s.getId(), s.getFecha(), rutinaId)).toList();
    }

    /**
     * Crea una sesión vinculándola a una rutina y verificando al usuario.
     */
    @Transactional
    public SesionResponse create(Long usuarioId, Long rutinaId, SesionRequest dto) {
        // Buscamos la rutina padre
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        // Valida que no exista una sesión con la misma fecha en la misma rutina
        if (sesionRepository.existsByRutinaIdAndFecha(rutinaId, dto.fecha())) {
            throw new RuntimeException("Ya existe una sesión registrada para el día de hoy en esta rutina.");
        }

        // Asignar los valores a la nueva sesión
        Sesion nuevaSesion = new Sesion();
        nuevaSesion.setFecha(dto.fecha());
        nuevaSesion.setRutina(rutina);

        // Copiamos la última sesión en caso de que exista
        Optional<Sesion> ultima = sesionRepository.findFirstByRutinaIdOrderByFechaDesc(rutinaId);

        // Guardar
        nuevaSesion = sesionRepository.save(nuevaSesion);

        if (ultima.isPresent()) {
            for (Ejercicio ej : ultima.get().getEjercicios()) {
                Ejercicio nuevo = new Ejercicio();
                nuevo.setSesion(nuevaSesion);
                nuevo.setEjercicioInfo(ej.getEjercicioInfo());
                nuevo.setParam1(ej.getParam1());
                nuevo.setParam2(ej.getParam2());
                ejercicioRepository.save(nuevo);
            }
        }
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