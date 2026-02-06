package com.example.rutinafit.service;

import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.repository.AmistadRepository;
import com.example.rutinafit.repository.SolicitudRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmistadService {

    private final AmistadRepository amistadRepository;
    private final SolicitudRepository solicitudRepository;

    public List<Amistad> listarAmigos(Long usuarioId) {
        return amistadRepository.findAllByUsuarioId(usuarioId);
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

        solicitudRepository.borrarSolicitudAmistad(
            amistad.getUsuario1().getId(), 
            amistad.getUsuario2().getId()
        );

        amistadRepository.deleteById(amistadId);
    }
}