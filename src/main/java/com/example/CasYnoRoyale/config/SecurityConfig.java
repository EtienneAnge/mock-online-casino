package com.example.CasYnoRoyale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. L'OUTIL DE HACHAGE (Le "Bean")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. LA CONFIGURATION (Pour ne pas être bloqué)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactive la protection CSRF (pour simplifier le dév des formulaires)
            .csrf(csrf -> csrf.disable()) 
            // Autorise TOUTES les requêtes (car tu as ton SessionInterceptor qui protège déjà)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Explicite pour H2 (bonne pratique)
                .anyRequest().permitAll() 
            )

            // 3. --- C'EST ICI QU'IL MANQUAIT LE CODE ---
            // Autoriser l'affichage dans des Frames (Obligatoire pour H2)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // -------------------------------------------

            // Désactive le formulaire de login moche de Spring
            .formLogin(form -> form.disable()) 
            // Désactive le logout par défaut de Spring
            .logout(logout -> logout.disable()); 

        return http.build();
    }
}