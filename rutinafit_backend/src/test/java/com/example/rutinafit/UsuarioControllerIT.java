package com.example.rutinafit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.rutinafit.model.Amistad;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.AmistadRepository;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.service.JwtService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UsuarioControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AmistadRepository amistadRepository;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    @DisplayName("Crear y buscar usuario")
    void testCrearYBuscarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("marcos");
        usuario.setEmail("marcos@test.com");
        usuario.setPassword("password123");
        usuario.setEsEntrenador(true);
        
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/usuarios/buscar")
                .param("username", "marcos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("marcos"));
    }

    @Test
    @DisplayName("El sistema rechaza un registro con email inválido")
    void testValidacionEmailIncorrecto() throws Exception {
        String usuarioInvalido = """
            {
                "username": "pepe",
                "email": "esto-no-es-un-email",
                "password": "123"
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Validar que la BD mantiene la integridad de la relación de amistad")
    void testCrearYComprobarAmistad() throws Exception {
        Usuario u1 = new Usuario();
        u1.setUsername("paco");
        u1.setEmail("paco@correo.com");
        u1.setPassword("password1234");

        Usuario u2 = new Usuario();
        u2.setUsername("maria");
        u2.setEmail("maria@correo.com");
        u2.setPassword("password1234");

        usuarioRepository.save(u1);
        usuarioRepository.save(u2);

        Amistad amistad = new Amistad();
            amistad.setUsuario1(u1);
            amistad.setUsuario2(u2);
        amistadRepository.save(amistad);

        
        boolean sonAmigos = amistadRepository.sonAmigos(u1.getId(), u2.getId());
        assertTrue(sonAmigos, "La relación de amistad debe existir en la DB");
    }
}