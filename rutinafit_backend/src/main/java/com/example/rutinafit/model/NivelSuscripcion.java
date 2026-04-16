package com.example.rutinafit.model;

public enum NivelSuscripcion {
    GRATIS(10, 0, 10), // 10 rutinas, 0 alumnos, 10 amigos
    TIER1(20, 10, 20), // 20 rutinas, 10 alumnos, 20 amigos
    TIER2(Integer.MAX_VALUE, 50, Integer.MAX_VALUE); // Ilimitado

    private final int limiteRutinas;
    private final int limiteAlumnos;
    private final int limiteAmigos;

    NivelSuscripcion(int limiteRutinas, int limiteAlumnos, int limiteAmigos) {
        this.limiteRutinas = limiteRutinas;
        this.limiteAlumnos = limiteAlumnos;
        this.limiteAmigos = limiteAmigos;
    }

    // Getters
    public int getLimiteRutinas() {
        return limiteRutinas;
    }

    public int getLimiteAlumnos() {
        return limiteAlumnos;
    }

    public int getLimiteAmigos() {
        return limiteAmigos;
    }
}