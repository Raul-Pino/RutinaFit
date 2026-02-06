package com.example.rutinafit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SolicitudRequest(
    @NotNull(message = "El ID del destinatario es obligatorio")
    Long destinatarioId,

    @NotBlank(message = "El tipo de solicitud no puede estar vacío")
    @Pattern(regexp = "AMISTAD|ENTRENAMIENTO", message = "El tipo debe ser AMISTAD o ENTRENAMIENTO")
    String tipo
) {}