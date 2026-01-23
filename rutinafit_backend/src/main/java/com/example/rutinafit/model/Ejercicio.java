package com.example.rutinafit.model;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejercicio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ejercicio_info_id", nullable = false)
    private EjercicioInfo ejercicioInfo;

    @NotNull(message = "El valor 1 es obligatorio")
    private Double param1;

    @NotNull(message = "El valor 2 es obligatorio")
    private Double param2;
}