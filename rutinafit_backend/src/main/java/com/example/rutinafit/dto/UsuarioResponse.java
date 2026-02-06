package com.example.rutinafit.dto;

public record UsuarioResponse(
    Long id,
    String username,
    boolean esEntrenador
) {}