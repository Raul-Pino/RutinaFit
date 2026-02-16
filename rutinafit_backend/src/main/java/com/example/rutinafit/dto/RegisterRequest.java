package com.example.rutinafit.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Length(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    String username,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Length(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password
) {}
