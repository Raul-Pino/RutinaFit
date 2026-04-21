package com.example.rutinafit.dto;

public record UsuarioResponse(
    Long id,
    String username,
    String email,
    String rol,
    String fotoPerfil,
    Boolean esEntrenador
) {}