package com.example.rutinafit.repository;

import com.example.rutinafit.model.Solicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByDestinatarioIdAndEstado(Long destinatarioId, String estado);
    boolean existsByRemitenteIdAndDestinatarioIdAndEstado(Long remId, Long destId, String estado);

    // @Query("SELECT s FROM Solicitud s WHERE s.destinatario.id = :destId " +
    //     "AND s.estado = :estado " +
    //     "AND s.fechaCreacion > :fechaLimite")
    // List<Solicitud> findRecientes(Long destId, String estado, LocalDateTime fechaLimite);

    // @Transactional // Operación para borrar solicitudes pendientes después de 30 días
    // void deleteByFechaCreacionBefore(LocalDateTime fecha);

    @Modifying
    @Query("DELETE FROM Solicitud s WHERE s.tipo = :tipo AND " +
        "((s.remitente.id = :u1 AND s.destinatario.id = :u2) OR " +
        " (s.remitente.id = :u2 AND s.destinatario.id = :u1))")
    void borrarSolicitud(Long u1, Long u2, String tipo);
    
    @Query("SELECT COUNT(s) > 0 FROM Solicitud s WHERE s.tipo = :tipo AND " +
        "((s.remitente.id = :u1 AND s.destinatario.id = :u2) OR " +
        " (s.remitente.id = :u2 AND s.destinatario.id = :u1))")
    boolean existsByUsuariosYTipo(Long u1, Long u2, String tipo);
}