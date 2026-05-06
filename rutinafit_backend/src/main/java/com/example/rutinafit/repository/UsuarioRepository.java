package com.example.rutinafit.repository;

import com.example.rutinafit.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByUsernameContainingIgnoreCase(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByToken(String token);

    @Query(value = "SELECT * FROM usuario WHERE rol != 'ADMIN'", nativeQuery = true)
    List<Usuario> findAllUsers();

    long countByEntrenadorId(Long id);

    List<Usuario> findByEsEntrenadorTrue();

    List<Usuario> findByEntrenadorId(Long entrenadorId);
}
