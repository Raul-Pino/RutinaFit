package com.example.rutinafit.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "amistades")
public class Amistad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id_1", nullable = false)
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario_id_2", nullable = false)
    private Usuario usuario2;

    private LocalDateTime fechaAmistad = LocalDateTime.now();

    public Amistad(Usuario u1, Usuario u2) {
        this.usuario1 = u1;
        this.usuario2 = u2;
    }
}