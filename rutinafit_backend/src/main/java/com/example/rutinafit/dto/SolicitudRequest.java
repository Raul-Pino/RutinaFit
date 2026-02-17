package com.example.rutinafit.dto;

import com.example.rutinafit.model.TipoSolicitud;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record SolicitudRequest(
    @NotNull(message = "El ID del destinatario es obligatorio")
    Long destinatarioId,

    @Enumerated(EnumType.STRING)
    TipoSolicitud tipo
) {}