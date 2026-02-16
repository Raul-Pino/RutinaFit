package com.example.rutinafit.model;

import java.util.List;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Length(min = 2, max = 60)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Length(min = 6)
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String rol = "USER";

    @Column(nullable = false)
    private boolean esEntrenador = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_suscripcion")
    private NivelSuscripcion nivelSuscripcion = NivelSuscripcion.GRATIS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id")
    @JsonBackReference(value = "usuario-entrenador")
    @ToString.Exclude 
    private Usuario entrenador;

    @OneToMany(mappedBy = "entrenador")
    @JsonManagedReference(value = "usuario-entrenador")
    @ToString.Exclude
    private List<Usuario> entrenados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usuario-rutina")
    @ToString.Exclude
    private List<Rutina> rutinas;
}

