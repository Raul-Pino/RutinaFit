package com.example.rutinafit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EjercicioRequest {
    
    // El ID del tipo de ejercicio (ej: 5 = Press Banca)
    @NotNull(message = "Debes indicar el tipo de ejercicio")
    private Long ejercicioInfoId;

    // param1: Puede ser Peso (kg) o Distancia (km)
    @NotNull
    private Double param1;

    // param2: Puede ser Repeticiones o Tiempo (min)
    @NotNull
    private Double param2;
}