package com.example.rutinafit.repository;

import com.example.rutinafit.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByDestinatarioIdAndEstado(Long destinatarioId, String estado);
    boolean existsByRemitenteIdAndDestinatarioIdAndEstado(Long remId, Long destId, String estado);
    @Query("SELECT s FROM Solicitud s WHERE s.destinatario.id = :destId " +
        "AND s.estado = :estado " +
        "AND s.fechaCreacion > :fechaLimite")
    List<Solicitud> findRecientes(Long destId, String estado, LocalDateTime fechaLimite);

    @Transactional // Operación para borrar solicitudes después de 30 días
    void deleteByFechaCreacionBefore(LocalDateTime fecha);
}