package com.example.rutinafit.repository;

import com.example.rutinafit.model.Amistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AmistadRepository extends JpaRepository<Amistad, Long> {
    @Query("SELECT a FROM Amistad a WHERE a.usuario1.id = :userId OR a.usuario2.id = :userId")
    List<Amistad> findAllByUsuarioId(Long userId);
}