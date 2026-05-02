package com.example.rutinafit.dto;

public record UsuarioBuscarResponse(
    Long id,
    String username,
    String email,
    String rol,
    String fotoPerfil,
    Boolean esEntrenador,
    Boolean esAmigo
){}