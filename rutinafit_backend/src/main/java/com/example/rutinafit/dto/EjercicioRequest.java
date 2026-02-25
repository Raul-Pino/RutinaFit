package com.example.rutinafit.dto;

import jakarta.validation.constraints.NotNull;

public record EjercicioRequest(
    // El ID del tipo de ejercicio (ej: 5 = Press Banca)
    @NotNull(message = "Debes indicar el tipo de ejercicio")
    Long ejercicioInfoId,

    // param1: Puede ser Peso (kg) o Distancia (km)
    @NotNull
    Double param1,

    // param2: Puede ser Repeticiones o Tiempo (min)
    @NotNull
    Double param2
) {}