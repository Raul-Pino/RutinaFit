package com.example.rutinafit.service;

import com.example.rutinafit.dto.EjercicioRequest;
import com.example.rutinafit.model.Ejercicio;
import com.example.rutinafit.model.EjercicioInfo;
import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.repository.EjercicioInfoRepository;
import com.example.rutinafit.repository.EjercicioRepository;
import com.example.rutinafit.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final SesionRepository sesionRepository;
    private final EjercicioInfoRepository ejercicioInfoRepository;

    /**
     * Listar ejercicios de una sesión
     */
    public List<Ejercicio> findBySesionId(Long usuarioId, Long sesionId) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Seguridad
        if (!sesion.getRutina().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para ver esta sesión");
        }

        return ejercicioRepository.findBySesionId(sesionId);
    }

    /**
     * Crear ejercicio
     */
    public Ejercicio create(Long usuarioId, Long sesionId, EjercicioRequest request) {
        
        // 1. Validar Sesión y Usuario
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!sesion.getRutina().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No puedes editar una sesión ajena");
        }

        // 2. Validar Tipo de Ejercicio
        EjercicioInfo info = ejercicioInfoRepository.findById(request.getEjercicioInfoId())
                .orElseThrow(() -> new RuntimeException("El tipo de ejercicio no existe"));

        // 3. Crear Entidad
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setSesion(sesion);
        ejercicio.setEjercicioInfo(info);
        
        // Aquí asignamos los valores genéricos
        ejercicio.setParam1(request.getParam1()); 
        ejercicio.setParam2(request.getParam2());

        return ejercicioRepository.save(ejercicio);
    }

    /*
    * Actualizar ejercicio
    */
    public Ejercicio update(Long usuarioId, Long ejercicioId, EjercicioRequest request) {
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        if (!ejercicio.getSesion().getRutina().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        ejercicio.setParam1(request.getParam1());
        ejercicio.setParam2(request.getParam2());

        if (!ejercicio.getEjercicioInfo().getId().equals(request.getEjercicioInfoId())) {
            EjercicioInfo info = ejercicioInfoRepository.findById(request.getEjercicioInfoId())
                    .orElseThrow(() -> new RuntimeException("El tipo de ejercicio no existe"));
            ejercicio.setEjercicioInfo(info);
        }

        return ejercicioRepository.save(ejercicio);
    }

    /**
     * Eliminar ejercicio
     */
    public void delete(Long usuarioId, Long ejercicioId) {
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        if (!ejercicio.getSesion().getRutina().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        ejercicioRepository.deleteById(ejercicioId);
    }
}