package com.example.rutinafit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RutinaRequest(
    @NotBlank(message = "El nombre de la rutina es obligatorio")
    @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    String nombre,
    
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    String descripcion
) {}
