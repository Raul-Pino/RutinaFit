package com.example.rutinafit.dto;

public record EjercicioResponse(
    Long id,
    String nombreEjercicio, // EjercicioInfo
    Double param1, // Peso o distancia
    Double param2, // Repeticiones o tiempo
    String descripcion,
    int codigoTipo // EjercicioInfo
) {}
