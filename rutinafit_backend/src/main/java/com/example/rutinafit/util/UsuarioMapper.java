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
        //if(u.getFotoPerfil() == null){
            String fotoPerfil = "https://ui-avatars.com/api/?name=" + u.getUsername() + 
                                "&background=random&color=fff&size=128";
        //}else{
            
        //}
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol(),fotoPerfil, u.isEsEntrenador());
    }

    public UsuarioBuscarResponse pasarABuscarDTO(Usuario u, boolean esAmigo){
        String fotoPerfil = "https://ui-avatars.com/api/?name=" + u.getUsername() + 
                                "&background=random&color=fff&size=128";
        return new UsuarioBuscarResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol(),fotoPerfil, u.isEsEntrenador(), esAmigo);
    }

}
