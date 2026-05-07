package com.example.rutinafit.util;

import org.springframework.stereotype.Component;

import com.example.rutinafit.dto.UsuarioBuscarResponse;
import com.example.rutinafit.dto.UsuarioResponse;
import com.example.rutinafit.model.Usuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {


    // "Transforma" un objeto usuario a un objeto que puede enviarse al frontEnd
    // (Eliminando información sensible)
    public UsuarioResponse pasarADTO(Usuario u){
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol(),u.getFotoPerfil(), u.isEsEntrenador());
    }

    public UsuarioBuscarResponse pasarABuscarDTO(Usuario u, boolean esAmigo){
        return new UsuarioBuscarResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol(),u.getFotoPerfil(), u.isEsEntrenador(), esAmigo);
    }

}
