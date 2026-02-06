package com.example.rutinafit.repository;

import com.example.rutinafit.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Transactional // Operación para borrar solicitudes pendientes después de 30 días
    void deleteByFechaCreacionBefore(LocalDateTime fecha);

    @Modifying
    @Query("DELETE FROM Solicitud s WHERE s.tipo = 'AMISTAD' AND " +
            "((s.remitente.id = :u1 AND s.destinatario.id = :u2) OR " +
            " (s.remitente.id = :u2 AND s.destinatario.id = :u1))")
    void borrarSolicitudAmistad(Long u1, Long u2);

    @Modifying
    @Transactional
    @Query("DELETE FROM Solicitud s WHERE s.tipo = 'ENTRENAMIENTO' AND " +
        "((s.remitente.id = :u1 AND s.destinatario.id = :u2) OR " +
        " (s.remitente.id = :u2 AND s.destinatario.id = :u1))")
    void borrarSolicitudEntrenamiento(@Param("u1") Long u1, @Param("u2") Long u2);
}