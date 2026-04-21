package com.example.rutinafit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EjercicioInfoRequest(
    @Min(1) @Max(10)
    int codigo,
    
    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(min = 2, max = 60)
    String nombre,
    
    @Size(max = 100)
    String descripcion
) {}
