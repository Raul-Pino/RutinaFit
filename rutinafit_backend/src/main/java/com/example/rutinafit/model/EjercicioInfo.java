package com.example.rutinafit.model;

import org.hibernate.validator.constraints.Length;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Table(name = "ejercicio_info")
public class EjercicioInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejercicio_info_id")
    private Long id;

    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private int codigo;

    @Length(min = 2, max = 60)
    @Column(nullable = false, length = 60)
    private String nombre;

    @Length(max = 100)
    @Column(length = 100)
    private String descripcion;

    @Column(name = "enlace_explicacion", length = 255)
    private String enlaceExplicacion = null;
}