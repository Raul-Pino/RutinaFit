package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.AmistadRepository;
import com.example.rutinafit.repository.SolicitudRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmistadService {

    private final AmistadRepository amistadRepository;
    private final SolicitudRepository solicitudRepository;

    public List<UsuarioResponse> listarMisAmigos(Long userId) {
        List<Amistad> amistades = amistadRepository.findAllByUsuarioId(userId);

        List<UsuarioResponse> respuesta = new ArrayList<>();

        for(Amistad a : amistades){
            Usuario amigo;
            if(a.getUsuario1().getId().equals(userId)) amigo = a.getUsuario2();
            else amigo = a.getUsuario1();
            respuesta.add(new UsuarioResponse(amigo.getId(), amigo.getUsername(), amigo.isEsEntrenador()));
        }
        return respuesta;
    }

    @Transactional
    public void eliminarAmistad(Long amistadId, Long usuarioId) {
        Amistad amistad = amistadRepository.findById(amistadId)
                .orElseThrow(() -> new RuntimeException("Relación de amistad no encontrada"));

        // Solo los dueños de la amistad pueden borrarla
        if (!amistad.getUsuario1().getId().equals(usuarioId) && 
            !amistad.getUsuario2().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta amistad");
        }

        solicitudRepository.borrarSolicitud(
            amistad.getUsuario1().getId(), 
            amistad.getUsuario2().getId(),
            "AMISTAD"
        );

        amistadRepository.deleteById(amistadId);
    }
}