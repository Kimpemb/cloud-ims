package com.joshuawilliams.ims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot backend.
 *
 * Replaces the old JavaFX MainApp.java as the application's launcher.
 * MainApp.java and the rest of the JavaFX ui/ package move to _archive/
 * rather than being deleted (see PHASES.md Phase 2).
 */
@SpringBootApplication
public class CloudImsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudImsApplication.class, args);
    }
}