package com.example.rutinafit.dto;

import java.time.LocalDateTime;

public record SolicitudResponse(
    Long id,
    String remitenteNombre,
    String tipo,
    LocalDateTime fecha
){}
