package com.example.rutinafit.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

public record SesionRequest(
    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate fecha
) {}