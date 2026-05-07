package com.example.rutinafit.model;

import java.util.List;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rutinas")
public class Rutina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rutina_id")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Length(min = 2, max = 60)
    @Column(nullable = false)
    private String nombre;

    @Column(name = "foto_rutina")
    private String fotoRutina = null;

    @Length(max = 255)
    @Column(length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "usuario_id", nullable = false) 
    @JsonBackReference
    @ToString.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Sesion> sesiones;
}