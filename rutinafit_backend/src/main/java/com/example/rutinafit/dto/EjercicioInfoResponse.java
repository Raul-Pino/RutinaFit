package com.example.rutinafit.dto;

public record EjercicioInfoResponse(
    Long id,
    int codigo,
    String nombre,
    String descripcion,
    String enlaceExplicacion
) {}