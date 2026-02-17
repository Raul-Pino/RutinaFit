package com.example.rutinafit.repository;

import com.example.rutinafit.model.EstadoSolicitud;
import com.example.rutinafit.model.Solicitud;
import com.example.rutinafit.model.TipoSolicitud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByDestinatarioIdAndEstado(Long destinatarioId, EstadoSolicitud estado);
    boolean existsByRemitenteIdAndDestinatarioIdAndEstado(Long remId, Long destId, EstadoSolicitud estado);

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
    void borrarSolicitud(@Param("u1") Long u1, @Param("u2") Long u2, @Param("tipo") TipoSolicitud tipo);

    @Query("SELECT COUNT(s) > 0 FROM Solicitud s WHERE s.tipo = :tipo AND " +
        "((s.remitente.id = :u1 AND s.destinatario.id = :u2) OR " +
        " (s.remitente.id = :u2 AND s.destinatario.id = :u1))")
    boolean existsByUsuariosYTipo(@Param("u1") Long u1, @Param("u2") Long u2, @Param("tipo") TipoSolicitud tipo);
}