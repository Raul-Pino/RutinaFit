package com.example.rutinafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Tareas programadas (Borrar solicitudes después de 30 días)
public class RutinafitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RutinafitBackendApplication.class, args);
	}

}