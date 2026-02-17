package com.example.rutinafit.service;

import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.TipoSolicitud;
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
        List<Usuario> amistades = amistadRepository.buscarPorUsuarioId(userId);

        List<UsuarioResponse> respuesta = new ArrayList<>();

        /*
        for(Amistad a : amistades){
            Usuario amigo;
            if(a.getUsuario1().getId().equals(userId)) amigo = a.getUsuario2();
            else amigo = a.getUsuario1();
            respuesta.add(new UsuarioResponse(amigo.getId(), amigo.getUsername(), amigo.isEsEntrenador()));
        }
        */
    
        for(Usuario amigo : amistades){
            respuesta.add(new UsuarioResponse(amigo.getId(), amigo.getUsername(), amigo.isEsEntrenador()));
        }

        return respuesta;
    }

    @Transactional
    public void eliminarAmistad(Long amigoId, Long usuarioId){
        Amistad amistad = amistadRepository.buscarAmistadEntre(usuarioId, amigoId)
                    .orElseThrow(() -> new RuntimeException("No existe una relación de amistad entre estos usuarios"));
        
        solicitudRepository.borrarSolicitud(amigoId, usuarioId, TipoSolicitud.AMISTAD);
        
        amistadRepository.delete(amistad);
    }
}