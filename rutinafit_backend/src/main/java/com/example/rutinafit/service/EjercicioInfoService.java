package com.example.rutinafit.service;

import com.example.rutinafit.dto.EjercicioInfoRequest;
import com.example.rutinafit.dto.EjercicioInfoResponse;
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
    public List<EjercicioInfoResponse> findAll(){
        return ejercicioInfoRepository.findAll().stream().map(e -> transformarADto(e)).toList();
    }

    /*
    * Crear nuevo ejercicio en el catálogo
    */
    public EjercicioInfoResponse create(EjercicioInfoRequest dto){
        EjercicioInfo info = new EjercicioInfo();
        info.setCodigo(dto.codigo());
        info.setNombre(dto.nombre());
        info.setDescripcion(dto.descripcion());
        
        return transformarADto(ejercicioInfoRepository.save(info));
    }

    /*
    * Actualizar información de ejercicio existente
    */
    public EjercicioInfoResponse update(Long id, EjercicioInfoRequest dto) {
        EjercicioInfo existente = ejercicioInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Información de ejercicio no encontrada"));

        existente.setNombre(dto.nombre());
        existente.setDescripcion(dto.descripcion());
        existente.setCodigo(dto.codigo());

        return transformarADto(ejercicioInfoRepository.save(existente));
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

    /**
     * Buscar el ejercicio info que contenga el nombre
     */
    public List<EjercicioInfoResponse> buscarEjercicioInfo(String nombre){
        return ejercicioInfoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                    .map(e -> transformarADto(e)).toList();
    }



    private EjercicioInfoResponse transformarADto(EjercicioInfo e) {
        return new EjercicioInfoResponse(e.getId(), e.getCodigo(), e.getNombre(), e.getDescripcion());
    }


}