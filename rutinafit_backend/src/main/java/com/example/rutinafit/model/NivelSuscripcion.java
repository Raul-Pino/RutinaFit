package com.example.rutinafit.model;

public enum NivelSuscripcion {
    GRATIS(5, 3), // 5 rutinas, 3 alumnos
    TIER1(20, 10), // 20 rutinas, 10 alumnos
    TIER2(30, 30); // 30 rutinas, 30 alumnos

    private final int limiteRutinas;
    private final int limiteAlumnos;

    NivelSuscripcion(int limiteRutinas, int limiteAlumnos) {
        this.limiteRutinas = limiteRutinas;
        this.limiteAlumnos = limiteAlumnos;
    }

    // Getters
    public int getLimiteRutinas() {
        return limiteRutinas;
    }

    public int getLimiteAlumnos() {
        return limiteAlumnos;
    }
}