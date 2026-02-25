package com.example.rutinafit.repository;

import com.example.rutinafit.model.EjercicioInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjercicioInfoRepository extends JpaRepository<EjercicioInfo, Long> {
    List<EjercicioInfo> findByNombreContainingIgnoreCase(String nombre);
}
