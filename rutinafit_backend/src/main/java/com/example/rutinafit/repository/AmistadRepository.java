package com.example.rutinafit.repository;

import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AmistadRepository extends JpaRepository<Amistad, Long> {
        @Query("SELECT COUNT(a) > 0 FROM Amistad a WHERE " +
                "(a.usuario1.id = :u1 AND a.usuario2.id = :u2) OR " +
                "(a.usuario1.id = :u2 AND a.usuario2.id = :u1)")
        boolean sonAmigos(@Param("u1") Long u1, @Param("u2") Long u2);

        /*
        @Query("SELECT a FROM Amistad a WHERE a.usuario1.id = :userId OR a.usuario2.id = :userId")
        List<Amistad> buscarPorUsuarioId(@Param("userId") Long userId);
        */

        @Query("SELECT u FROM Usuario u WHERE u.id IN (" +
        "  SELECT CASE WHEN a.usuario1.id = :userId THEN a.usuario2.id ELSE a.usuario1.id END " +
        "  FROM Amistad a WHERE a.usuario1.id = :userId OR a.usuario2.id = :userId" +
        ")")
        List<Usuario> buscarPorUsuarioId(@Param("userId") Long userId);

        @Query("SELECT a FROM Amistad a WHERE " +
                "(a.usuario1.id = :u1 AND a.usuario2.id = :u2) OR " +
                "(a.usuario1.id = :u2 AND a.usuario2.id = :u1)")
        Optional<Amistad> buscarAmistadEntre(@Param("u1") Long u1, @Param("u2") Long u2);
}