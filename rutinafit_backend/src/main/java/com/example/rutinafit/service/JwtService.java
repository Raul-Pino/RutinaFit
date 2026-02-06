package com.example.rutinafit.service;

//import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_STRING = "secreto_super_seguro_que_tiene_que_ser_largo_123456";
    private final Key secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generarToken(String username, String rol, String email, Long id) {
        long ahora = System.currentTimeMillis();
        long expiracion = 1000 * 60 * 60; // 1 hora

        return Jwts.builder()
            .setSubject(username)
            .claim("rol", rol)
            .claim("email", email)
            .claim("id", id)
            .setIssuedAt(new Date(ahora))
            .setExpiration(new Date(ahora + expiracion))
            .signWith(secretKey)
            .compact();
    }

    public String validarYObtenerUsuario(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public Long obtenerId(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("id", Long.class); 
        } catch (Exception e) {
            return null;
        }
    }

    public String obtenerRol(String token) {
        try {
            return getClaims(token).get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String obtenerEmail(String token) {
        try {
            return getClaims(token).get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean esAdmin(String token) {
        String rol = obtenerRol(token);
        return "ADMIN".equalsIgnoreCase(rol);
    }

    // Método auxiliar para no repetir código
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}