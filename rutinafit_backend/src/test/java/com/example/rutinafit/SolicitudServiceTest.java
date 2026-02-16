package com.example.rutinafit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.rutinafit.dto.SolicitudRequest;
import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.Solicitud;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.AmistadRepository;
import com.example.rutinafit.repository.SolicitudRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.service.SolicitudService;

// Imports estáticos para JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Imports estáticos para Mockito
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AmistadRepository amistadRepository;
    @InjectMocks
    private SolicitudService solicitudService;



    // Tests

    @Test
    @DisplayName("Debe lanzar excepción si el remitente y destinatario son el mismo")
    void testEnviarSolicitudAMismoUsuario(){
        Long miId = 2L;
        SolicitudRequest dto = new SolicitudRequest(miId, "AMISTAD");

        Usuario usuario = new Usuario();
        usuario.setId(miId);
        when(usuarioRepository.findById(miId)).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitudService.enviarSolicitud(miId, dto);
        });

        assertEquals("No te puedes enviar una solicitud a ti mismo", exception.getMessage());
        verify(solicitudRepository, never()).save(any());
    }


    @Test
    @DisplayName("Debe fallar si ya existe una solicitud activa entre los usuarios")
    void testEnviarSolicitudDuplicada() {
        Long remitenteId = 1L;
        Long destinatarioId = 2L;
        SolicitudRequest dto = new SolicitudRequest(destinatarioId, "AMISTAD");

        Usuario remitente = new Usuario();
        remitente.setId(remitenteId);
        when(usuarioRepository.findById(remitenteId)).thenReturn(Optional.of(remitente));

        Usuario destinatario = new Usuario();
        destinatario.setId(destinatarioId);
        when(usuarioRepository.findById(destinatarioId)).thenReturn(Optional.of(destinatario));

        when(solicitudRepository.existsByUsuariosYTipo(remitenteId, destinatarioId, "AMISTAD"))
            .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitudService.enviarSolicitud(remitenteId, dto);
        });

        assertEquals("Ya existe una solicitud pendiente o activa entre vosotros", exception.getMessage());
    }

    @Test
    @DisplayName("Debe fallar si se envía solicitud de ENTRENAMIENTO a un usuario que no es entrenador")
    void testSolicitudEntrenamiento() {
        Long remitenteId = 1L;
        Long destinatarioId = 2L;
        SolicitudRequest dto = new SolicitudRequest(destinatarioId, "ENTRENAMIENTO");

        Usuario remitente = new Usuario();
        remitente.setId(remitenteId);

        Usuario destinatarioFalso = new Usuario();
        destinatarioFalso.setId(destinatarioId);
        destinatarioFalso.setEsEntrenador(false);

        when(usuarioRepository.findById(remitenteId)).thenReturn(Optional.of(remitente));
        when(usuarioRepository.findById(destinatarioId)).thenReturn(Optional.of(destinatarioFalso));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitudService.enviarSolicitud(remitenteId, dto);
        });

        assertEquals("Este usuario no es entrenador", exception.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe crear una Amistad y eliminar la Solicitud cuando se acepta")
    void testAceptarSolicitud() {
        // GIVEN
        Long solicitudId = 100L;
        Usuario remitente = new Usuario(); remitente.setId(1L); remitente.setUsername("remitente");
        Usuario destinatario = new Usuario(); destinatario.setId(2L); destinatario.setUsername("destinatario");

        Solicitud sol = new Solicitud();
        sol.setId(solicitudId);
        sol.setRemitente(remitente);
        sol.setDestinatario(destinatario);
        sol.setTipo("AMISTAD");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(sol));

        solicitudService.aceptarSolicitud(solicitudId, destinatario.getId());

        verify(amistadRepository, times(1)).save(any(Amistad.class));
        verify(solicitudRepository, times(1)).save(any(Solicitud.class));
        verify(solicitudRepository, never()).delete(any());
    }
}
