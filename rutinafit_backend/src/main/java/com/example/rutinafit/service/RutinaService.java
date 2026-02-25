package com.example.rutinafit.service;

import com.example.rutinafit.dto.RutinaRequest;
import com.example.rutinafit.dto.RutinaResponse;
import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    /*
    * Devuelve todas las rutinas por su usuario ID
    */
    public List<RutinaResponse> findByUsuarioId(Long usuarioId) {
        // Verificamos que el usuario exista
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return rutinaRepository.findByUsuarioId(usuarioId).stream()
                        .map(r -> new RutinaResponse(r.getId(), r.getNombre(), r.getDescripcion()))
                        .toList();
    }

    public List<RutinaResponse> findByUsuarioId(Long usuarioId, long propietarioId){
        Usuario usuario = usuarioRepository.findById(propietarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        securityUtils.validarAcceso(usuario, usuarioId);
        
        return findByUsuarioId(propietarioId);
    }

    /*
    * Devuelve una rutina por su ID y el ID del usuario propietario
    */
    public RutinaResponse findByIdAndUsuarioId(Long rutinaId, Long usuarioId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (rutina.getUsuario().getId() != usuarioId) {
            throw new RuntimeException("No tienes permiso para ver esta rutina");
        }
        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion());
    }

    /*
    * Crea una nueva rutina para un usuario específico
    */
    public RutinaResponse create(Long usuarioId, Long propietarioId, RutinaRequest dto) {
        Usuario usuario = usuarioRepository.findById(propietarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(usuario, usuarioId);

        // Comprobamos la cantidad de Rutinas que tiene el usuario
        // para saber si puede o no tener más rutinas
        long cantidad = rutinaRepository.countByUsuarioId(propietarioId);
        if (cantidad >= usuario.getNivelSuscripcion().getLimiteRutinas()) {
                throw new RuntimeException("Límite alcanzado para el plan " + usuario.getNivelSuscripcion());
        }

        Rutina rutina = new Rutina();
        rutina.setNombre(dto.nombre());
        rutina.setDescripcion(dto.descripcion());
        rutina.setUsuario(usuario);
        
        rutina = rutinaRepository.save(rutina);
        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion());
    }

    /*
    * Actualizar una rutina existente
    */
    public RutinaResponse update(Long id, Long usuarioId, RutinaRequest dto){
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        rutina.setNombre(dto.nombre());
        rutina.setDescripcion(dto.descripcion());
        rutina = rutinaRepository.save(rutina);

        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion());
    }

    /*
    * Elimina una rutina por su ID
    */
    public void delete(Long id, Long usuarioId){
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        
        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        rutinaRepository.deleteById(id);
    }
}
