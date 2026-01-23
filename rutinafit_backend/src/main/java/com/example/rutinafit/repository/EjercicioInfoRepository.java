package com.example.rutinafit.repository;

import com.example.rutinafit.model.EjercicioInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioInfoRepository extends JpaRepository<EjercicioInfo, Long> {
    List<EjercicioInfo> findByCodigo(int codigo);
}
