package com.example.rutinafit.service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rutinafit.dto.SolicitudRequest;
import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.EstadoSolicitud;
import com.example.rutinafit.model.Solicitud;
import com.example.rutinafit.model.TipoSolicitud;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.AmistadRepository;
import com.example.rutinafit.repository.SolicitudRepository;
import com.example.rutinafit.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmistadRepository amistadRepository;

    @Transactional
    public void enviarSolicitud(Long remitenteId, SolicitudRequest dto) {
        Usuario remitente = usuarioRepository.findById(remitenteId)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioRepository.findById(dto.destinatarioId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        
        boolean existe = solicitudRepository.existsByUsuariosYTipo(remitenteId, dto.destinatarioId(), dto.tipo());
        if (existe) {
            throw new RuntimeException("Ya existe una solicitud pendiente o activa entre vosotros");
        }

        if(remitenteId == destinatario.getId()){
            throw new RuntimeException("No te puedes enviar una solicitud a ti mismo");
        }

        if (dto.tipo() == TipoSolicitud.AMISTAD){
                boolean yaSonAmigos = amistadRepository.sonAmigos(remitenteId, dto.destinatarioId());
                if (yaSonAmigos) {
                    throw new RuntimeException("Ya sois amigos, no puedes enviar otra solicitud.");
                }
        }else{if(!destinatario.isEsEntrenador()){
                throw new RuntimeException("Este usuario no es entrenador");
            }
        }
                
        Solicitud nueva = new Solicitud();
        nueva.setRemitente(remitente);
        nueva.setDestinatario(destinatario);
        nueva.setTipo(dto.tipo());
        nueva.setEstado(EstadoSolicitud.PENDIENTE);

        solicitudRepository.save(nueva);
    }

    /**
     * @Transactional asegura la "Atomicidad": 
     * Si alguna operación dentro del método falla (ej: error al crear la Amistad), 
     * se hace un Rollback automático y se deshacen todos los cambios previos 
     * Así evitamos que la base de datos quede en un estado inconsistente o con datos corruptos.
     */
    @Transactional
    public void aceptarSolicitud(Long solicitudId, Long usuarioLogueadoId) {
        Solicitud sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!sol.getDestinatario().getId().equals(usuarioLogueadoId)) {
            throw new RuntimeException("No tienes permiso para aceptar esta solicitud");
        }

        if (sol.getTipo() == TipoSolicitud.ENTRENAMIENTO) {
            Usuario entrenador = sol.getDestinatario(); 
            Usuario alumno = sol.getRemitente(); 

            // Comprobamos el límite de usuarios entrenados por una persona
            long alumnosActuales = usuarioRepository.countByEntrenadorId(entrenador.getId());
            if (alumnosActuales >= entrenador.getNivelSuscripcion().getLimiteAlumnos()) {
                throw new RuntimeException("Límite de alumnos alcanzado para tu suscripción.");
            }

            // Añadimos al usuario a la lista del entrenador
            alumno.setEntrenador(entrenador);
            usuarioRepository.save(alumno);
        }

        if (sol.getTipo() == TipoSolicitud.AMISTAD) {
            Amistad nuevaAmistad = new Amistad();
            nuevaAmistad.setUsuario1(sol.getRemitente());
            nuevaAmistad.setUsuario2(sol.getDestinatario());
            amistadRepository.save(nuevaAmistad);
        }

        sol.setEstado(EstadoSolicitud.ACEPTADA);
        solicitudRepository.save(sol);
    }

    public List<Solicitud> obtenerSolicitudesPendientes(Long usuarioId) {
    return solicitudRepository.findByDestinatarioIdAndEstado(usuarioId, EstadoSolicitud.PENDIENTE);
    }

    /**
     * Tarea automática que limpia la base de datos de solicitudes antiguas.
     * Cron: "segundo minuto hora día mes día-semana"
     * "0 0 3 * * *" = Todos los días a las 03:00:00 AM
     */
    // Para pruebas cada 5 minútos: @Scheduled(fixedRate = 300000)
    // @Scheduled(cron = "0 0 3 * * *")
    // public void limpiarSolicitudesCaducadas() {
    //     LocalDateTime limite = LocalDateTime.now().minusDays(30);
    //     solicitudRepository.deleteByFechaCreacionBefore(limite);
    //     System.out.println("Limpieza de solicitudes completada para fechas anteriores a: " + limite);
    // }

    public void rechazarSolicitud(Long solicitudId, Long usuarioLogueadoId) {
        Solicitud sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!sol.getDestinatario().getId().equals(usuarioLogueadoId)) {
            throw new RuntimeException("No autorizado");
        }

        solicitudRepository.delete(sol);
    }
}