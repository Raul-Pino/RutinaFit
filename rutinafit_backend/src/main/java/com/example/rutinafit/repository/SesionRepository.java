package com.example.rutinafit.repository;

import com.example.rutinafit.model.Sesion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    List<Sesion> findByRutinaId(Long rutinaId);

    Optional<Sesion> findFirstByRutinaIdOrderByFechaDesc(Long rutinaId);

    boolean existsByRutinaIdAndFecha(Long rutinaId, LocalDate fecha);
}
