package com.example.rutinafit.service;

import com.example.rutinafit.dto.EjercicioRequest;
import com.example.rutinafit.dto.EjercicioResponse;
import com.example.rutinafit.model.Ejercicio;
import com.example.rutinafit.model.EjercicioInfo;
import com.example.rutinafit.model.Sesion;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.EjercicioInfoRepository;
import com.example.rutinafit.repository.EjercicioRepository;
import com.example.rutinafit.repository.SesionRepository;
import com.example.rutinafit.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final SesionRepository sesionRepository;
    private final EjercicioInfoRepository ejercicioInfoRepository;
    private final SecurityUtils securityUtils;


    /**
     * Listar ejercicios de una sesión
     */
    public List<EjercicioResponse> findBySesionId(Long usuarioId, Long sesionId) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        securityUtils.validarAcceso(sesion.getRutina().getUsuario(), usuarioId);

        return ejercicioRepository.findBySesionId(sesionId).stream()
                    .map(e -> transformarADto(e))
                    .toList();
    }

    /**
     * Crear ejercicio
     */
    public EjercicioResponse create(Long usuarioId, Long sesionId, EjercicioRequest request) {
        // Validar Sesión y Usuario
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        securityUtils.validarAcceso(sesion.getRutina().getUsuario(), usuarioId);

        // Validar Tipo de Ejercicio
        EjercicioInfo info = ejercicioInfoRepository.findById(request.ejercicioInfoId())
                .orElseThrow(() -> new RuntimeException("El tipo de ejercicio no existe"));

        // Crear Ejercicio
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setSesion(sesion);
        ejercicio.setEjercicioInfo(info);
        ejercicio.setParam1(request.param1()); 
        ejercicio.setParam2(request.param2());

        ejercicio = ejercicioRepository.save(ejercicio);
        return transformarADto(ejercicio);
    }

    /*
    * Actualizar ejercicio
    */
    public EjercicioResponse update(Long usuarioId, Long ejercicioId, EjercicioRequest request) {
        // Validar Sesión y Usuario
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));


        // Valida el acceso si es el dueño o el entrenador
        Usuario propietario = ejercicio.getSesion().getRutina().getUsuario();
        securityUtils.validarAcceso(propietario, usuarioId);
        
        EjercicioInfo info = ejercicioInfoRepository.findById(request.ejercicioInfoId())
        .orElseThrow(() -> new RuntimeException("El tipo de ejercicio no existe"));
        ejercicio.setEjercicioInfo(info);
        ejercicio.setParam1(request.param1());
        ejercicio.setParam2(request.param2());

        ejercicio = ejercicioRepository.save(ejercicio);
        return transformarADto(ejercicio);
    }

    /**
     * Eliminar ejercicio
     */
    public void delete(Long usuarioId, Long ejercicioId) {
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        // Valida el acceso si es el dueño o el entrenador
        Usuario propietario = ejercicio.getSesion().getRutina().getUsuario();
        securityUtils.validarAcceso(propietario, usuarioId);

        ejercicioRepository.deleteById(ejercicioId);
    }


    // ===============
    // AUX
    // ===============

    private EjercicioResponse transformarADto(Ejercicio e){
        return new EjercicioResponse(
                        e.getId(), 
                        e.getEjercicioInfo().getNombre(), 
                        e.getParam1(), 
                        e.getParam2(), 
                        e.getEjercicioInfo().getDescripcion(),
                        e.getEjercicioInfo().getCodigo(),
                        e.getEjercicioInfo().getId(),
                        e.getEjercicioInfo().getEnlaceExplicacion()
                    );
    }
}