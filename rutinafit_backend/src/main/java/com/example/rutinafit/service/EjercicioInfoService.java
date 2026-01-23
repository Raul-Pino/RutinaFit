package com.example.rutinafit.service;

import com.example.rutinafit.model.EjercicioInfo;
import com.example.rutinafit.repository.EjercicioInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioInfoService {

    private final EjercicioInfoRepository ejercicioInfoRepository;

    /*
    * Listar todos los tipos de ejercicios
    */
    public List<EjercicioInfo> findAll(){
        return ejercicioInfoRepository.findAll();
    }
    
    /*
    * Buscar por código de ejercicio
    */
    public List<EjercicioInfo> findByCodigo(int codigo){
        return ejercicioInfoRepository.findByCodigo(codigo);
    }

    /*
    * Crear nuevo ejercicio en el catálogo
    */
    public EjercicioInfo create(EjercicioInfo ejercicioInfo){
        return ejercicioInfoRepository.save(ejercicioInfo);
    }

    /*
    * Actualizar información de ejercicio existente
    */
    public EjercicioInfo update(Long id, EjercicioInfo nuevosDatos) {
        EjercicioInfo existente = ejercicioInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Información de ejercicio no encontrada"));

        existente.setNombre(nuevosDatos.getNombre());
        existente.setDescripcion(nuevosDatos.getDescripcion());
        existente.setCodigo(nuevosDatos.getCodigo());

        return ejercicioInfoRepository.save(existente);
    }

    /*  
    * Eliminar información de ejercicio por ID
    */
    public void delete(Long id){
        if (!ejercicioInfoRepository.existsById(id)) {
            throw new RuntimeException("Información de ejercicio no encontrada");
        }
        ejercicioInfoRepository.deleteById(id);
    }
}