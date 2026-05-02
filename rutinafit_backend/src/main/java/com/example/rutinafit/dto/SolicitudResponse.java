package com.example.rutinafit.dto;

import java.time.LocalDateTime;

public record SolicitudResponse(
    Long id,
    UsuarioResponse remitente,
    String tipo,
    LocalDateTime fecha
){}
