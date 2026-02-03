package com.example.rutinafit.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.Solicitud;
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

    public Solicitud enviarSolicitud(Long remitenteId, Long destinatarioId, String tipo) {
        if (solicitudRepository.existsByRemitenteIdAndDestinatarioIdAndEstado(remitenteId, destinatarioId, "PENDIENTE")) {
            throw new RuntimeException("Ya existe una solicitud pendiente");
        }

        Usuario rem = usuarioRepository.findById(remitenteId).orElseThrow();
        Usuario dest = usuarioRepository.findById(destinatarioId).orElseThrow();

        Solicitud sol = new Solicitud();
        sol.setRemitente(rem);
        sol.setDestinatario(dest);
        sol.setTipo(tipo);
        sol.setEstado("PENDIENTE");
        return solicitudRepository.save(sol);
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

        if ("ENTRENAMIENTO".equals(sol.getTipo())) {
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

        else if ("AMISTAD".equals(sol.getTipo())) {
            amistadRepository.save(new Amistad(sol.getRemitente(), sol.getDestinatario()));
        }

        sol.setEstado("ACEPTADA");
        solicitudRepository.save(sol);
    }

    public List<Solicitud> obtenerSolicitudesPendientes(Long usuarioId) {
    LocalDateTime haceUnMes = LocalDateTime.now().minusDays(30);
    return solicitudRepository.findRecientes(usuarioId, "PENDIENTE", haceUnMes);
    }

    /**
     * Tarea automática que limpia la base de datos de solicitudes antiguas.
     * Cron: "segundo minuto hora día mes día-semana"
     * "0 0 3 * * *" = Todos los días a las 03:00:00 AM
     */
    // Para pruebas cada 5 minútos: @Scheduled(fixedRate = 300000)
    @Scheduled(cron = "0 0 3 * * *")
    public void limpiarSolicitudesCaducadas() {
        LocalDateTime limite = LocalDateTime.now().minusDays(30);
        solicitudRepository.deleteByFechaCreacionBefore(limite);
        System.out.println("Limpieza de solicitudes completada para fechas anteriores a: " + limite);
    }

    public void rechazarSolicitud(Long solicitudId, Long usuarioLogueadoId) {
        Solicitud sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!sol.getDestinatario().getId().equals(usuarioLogueadoId)) {
            throw new RuntimeException("No autorizado");
        }

        sol.setEstado("RECHAZADA");
        solicitudRepository.save(sol);
    }
}