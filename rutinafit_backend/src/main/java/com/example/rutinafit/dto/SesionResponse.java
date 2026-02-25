package com.example.rutinafit.dto;

import java.time.LocalDate;

public record SesionResponse(
    Long id, 
    LocalDate fecha,
    Long idRutina
){}
