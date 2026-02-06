package com.example.rutinafit.dto;

public record UsuarioUpdateRequest(
    String username,
    String email,
    Boolean esEntrenador
) {}