package com.example.rutinafit.service;

import com.example.rutinafit.dto.AuthResponse;
import com.example.rutinafit.dto.LoginRequest;
import com.example.rutinafit.dto.RegisterRequest;
import com.example.rutinafit.model.Usuario;
import com.example.rutinafit.repository.UsuarioRepository;
import com.example.rutinafit.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UsuarioRepository usuarioRepository;
        private final JwtService jwtService;
        private final PasswordEncoder encoder;
        private final SecurityUtils securityUtils;

        /**
         * Registra un usuario nuevo en la base de datos y devuelve el token.
         */
        public AuthResponse register(RegisterRequest request) {
                // 1. Validar que el usuario no exista ya
                if (usuarioRepository.existsByUsername(request.username())) {
                        throw new RuntimeException("El nombre de usuario ya existe");
                }
                if (usuarioRepository.existsByEmail(request.email())) {
                        throw new RuntimeException("El email ya está registrado");
                }
                if (!securityUtils.validarPassword(request.password())) {
                        throw new RuntimeException("La contraseña no cumple con los requisitos");
                }

                // 2. Construir el objeto Usuario
                Usuario usuario = new Usuario();
                usuario.setUsername(request.username());
                usuario.setEmail(request.email());

                // 3. ENCRIPTAR la contraseña antes de guardar
                usuario.setPassword(encoder.encode(request.password()));

                // 5. Guardar en Base de Datos
                Usuario usuarioGuardado = usuarioRepository.save(usuario);

                // 6. Generar el Token JWT
                // (Nota: Asegúrate de usar la versión de JwtService que acepta el ID)
                String token = generarToken(usuarioGuardado);

                // 7. Devolver respuesta
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        /**
         * Autentica un usuario existente y devuelve el token.
         */
        public AuthResponse login(LoginRequest request) {
                // 1. Buscar usuario por email
                Usuario usuario = usuarioRepository.findByEmail(request.email())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                if (!encoder.matches(request.password(), usuario.getPassword())) {
                        throw new RuntimeException("Contraseña incorrecta");
                }

                // 3. Generar Token
                String token = generarToken(usuario);

                return AuthResponse.builder().token(token).build();
        }

        /**
         * Generar un nuevo token a partir de un token válido antiguo.
         */
        public AuthResponse refreshToken(String authHeader) {
                // 1. Limpiar el prefijo "Bearer "
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new RuntimeException("Token inválido");
                }
                String oldToken = authHeader.substring(7);

                // 2. Extraer el email del token antiguo
                Long id = jwtService.obtenerId(oldToken);
                if (id == null) {
                        throw new RuntimeException("Error al procesar el token");
                }

                // 3. Buscar el usuario en la BD
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // 4. Generar un token nuevo
                String newToken = generarToken(usuario);

                return AuthResponse.builder()
                                .token(newToken)
                                .build();
        }

        private String generarToken(Usuario u) {
                return jwtService.generarToken(
                                u.getUsername(),
                                u.getRol(),
                                u.getEmail(),
                                u.getId(),
                                u.isEsEntrenador(),
                                u.getEntrenador() != null ? u.getEntrenador().getId() : null);
        }
}