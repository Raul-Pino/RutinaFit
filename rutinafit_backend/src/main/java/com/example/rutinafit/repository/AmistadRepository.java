package com.example.rutinafit.repository;

import com.example.rutinafit.model.Amistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AmistadRepository extends JpaRepository<Amistad, Long> {
    // @Query("SELECT a FROM Amistad a WHERE a.usuario1.id = :userId OR a.usuario2.id = :userId")
    // List<Amistad> findAllByUsuarioId(Long userId);

    @Query("SELECT COUNT(a) > 0 FROM Amistad a WHERE " +
        "(a.usuario1.id = :u1 AND a.usuario2.id = :u2) OR " +
        "(a.usuario1.id = :u2 AND a.usuario2.id = :u1)")
    boolean sonAmigos(Long u1, Long u2);

    @Query("SELECT a FROM Amistad a WHERE a.usuario1.id = :userId OR a.usuario2.id = :userId")
    List<Amistad> findAllByUsuarioId(@Param("userId") Long userId);
}