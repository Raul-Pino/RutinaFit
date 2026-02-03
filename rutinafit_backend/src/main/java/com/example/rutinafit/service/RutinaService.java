package com.example.rutinafit.service;

import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;

    /*
    * Devuelve todas las rutinas
    */
    public List<Rutina> findAll(){
        return rutinaRepository.findAll();
    }

    /*
    * Devuelve todas las rutinas por su usuario ID
    */
    public List<Rutina> findByUsuarioId(Long usuarioId) {
        // Verificamos que el usuario exista (opcional, pero buena práctica)
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return rutinaRepository.findByUsuarioId(usuarioId);
    }

    /*
    * Devuelve una rutina por su ID y el ID del usuario propietario
    */
    public Rutina findByIdAndUsuarioId(Long rutinaId, Long usuarioId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (!rutina.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para ver esta rutina");
        }
        return rutina;
    }

    /*
    * Crea una nueva rutina para un usuario específico
    */
    public Rutina create(Long usuarioId, Rutina rutina) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        long cantidad = rutinaRepository.countByUsuarioId(usuarioId);
        if (cantidad >= usuario.getNivelSuscripcion().getLimiteRutinas()) {
                throw new RuntimeException("Has alcanzado el límite de rutinas para tu plan " + usuario.getNivelSuscripcion());
        }
        
        rutina.setUsuario(usuario);
        return rutinaRepository.save(rutina);
    }

    /*
    * Actualizar una rutina existente
    */
    public Rutina update(Long id, Long usuarioId, Rutina nuevaRutina){
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (!rutina.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado para actualizar esta rutina");
        }

        rutina.setNombre(nuevaRutina.getNombre());
        rutina.setDescripcion(nuevaRutina.getDescripcion());

        return rutinaRepository.save(rutina);
    }

    /*
    * Elimina una rutina por su ID
    */
    public void delete(Long id, Long usuarioId){
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        if (!rutina.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado para eliminar esta rutina");
        }
        rutinaRepository.deleteById(id);
    }
}
