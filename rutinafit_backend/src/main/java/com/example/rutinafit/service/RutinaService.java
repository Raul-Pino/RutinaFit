package com.example.rutinafit.service;

import com.example.rutinafit.dto.RutinaRequest;
import com.example.rutinafit.dto.RutinaResponse;
import com.example.rutinafit.model.Rutina;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.RutinaRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.util.FilesUtils;
import com.example.rutinafit.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RutinaService {

    @Value("${app.url-back}")
    private String urlBack;
    @Value("${app.upload.dir}")
    private String uploadDir;

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;
    private final FilesUtils filesUtils;

    /*
    * Devuelve todas las rutinas por su usuario ID
    */
    @Transactional(readOnly = true)
    public List<RutinaResponse> findByUsuarioId(Long usuarioId) {
        // Verificamos que el usuario exista
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return rutinaRepository.findByUsuarioId(usuarioId).stream()
                        .map(r -> new RutinaResponse(r.getId(), r.getNombre(), r.getDescripcion(), r.getFotoRutina()))
                        .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaResponse> findByUsuarioId(Long usuarioId, long propietarioId){
        Usuario usuario = usuarioRepository.findById(propietarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        securityUtils.validarAcceso(usuario, usuarioId);
        
        return findByUsuarioId(propietarioId);
    }

    /*
    * Devuelve una rutina por su ID y el ID del usuario propietario
    */
    @Transactional(readOnly = true)
    public RutinaResponse findByIdAndUsuarioId(Long rutinaId, Long usuarioId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (rutina.getUsuario().getId() != usuarioId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta rutina");
        }
        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion(), rutina.getFotoRutina());
    }

    /*
    * Crea una nueva rutina para un usuario específico
    */
    public RutinaResponse create(Long usuarioId, Long propietarioId, RutinaRequest dto, MultipartFile fotoRutina) throws IOException {
        Usuario usuario = usuarioRepository.findById(propietarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(usuario, usuarioId);

        // Comprobamos la cantidad de Rutinas que tiene el usuario
        // para saber si puede o no tener más rutinas
        long cantidad = rutinaRepository.countByUsuarioId(propietarioId);
        if (cantidad >= usuario.getNivelSuscripcion().getLimiteRutinas()) {
                throw new RuntimeException("Límite alcanzado para el plan " + usuario.getNivelSuscripcion());
        }

        Rutina rutina = new Rutina();
        rutina.setNombre(dto.nombre());
        rutina.setDescripcion(dto.descripcion());
        rutina.setUsuario(usuario);
        
        if (fotoRutina != null && !fotoRutina.isEmpty()) {
            Path ruta = Paths.get(uploadDir + "/fotoRutinas");
            Files.createDirectories(ruta);
            String filename = "rutina_" + propietarioId + "_" + System.currentTimeMillis()
                            + filesUtils.getExtension(fotoRutina.getOriginalFilename());
            fotoRutina.transferTo(ruta.resolve(filename));
            rutina.setFotoRutina(urlBack + "/fotoRutinas/" + filename);
        }



        rutina = rutinaRepository.save(rutina);
        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion(), rutina.getFotoRutina());
    }

    /*
    * Actualizar una rutina existente
    */
    public RutinaResponse update(Long id, Long usuarioId, RutinaRequest dto, MultipartFile fotoRutina) throws IOException {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        if (fotoRutina != null && !fotoRutina.isEmpty()) {
            // Borrar la antigua solo si es un fichero local (no una URL externa)
            if (rutina.getFotoRutina() != null && rutina.getFotoRutina().startsWith(urlBack)) {
                String rutaAntigua = rutina.getFotoRutina().replace(urlBack, "");
                Path antigua = Paths.get(uploadDir + rutaAntigua);
                Files.deleteIfExists(antigua);
            }


            Path ruta = Paths.get(uploadDir + "/fotoRutinas");
            Files.createDirectories(ruta);
            String filename = "rutina_" + rutina.getUsuario().getId() + "_" + System.currentTimeMillis()
                            + filesUtils.getExtension(fotoRutina.getOriginalFilename());
            fotoRutina.transferTo(ruta.resolve(filename));
            rutina.setFotoRutina(urlBack + "/fotoRutinas/" + filename);
        }

        rutina.setNombre(dto.nombre());
        rutina.setDescripcion(dto.descripcion());
        rutina = rutinaRepository.save(rutina);

        return new RutinaResponse(rutina.getId(), rutina.getNombre(), rutina.getDescripcion(), rutina.getFotoRutina());
    }

    /*
    * Elimina una rutina por su ID
    */
    public void delete(Long id, Long usuarioId){
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        
        // Valida el acceso si es el dueño o el entrenador
        securityUtils.validarAcceso(rutina.getUsuario(), usuarioId);

        rutinaRepository.deleteById(id);
    }
}
