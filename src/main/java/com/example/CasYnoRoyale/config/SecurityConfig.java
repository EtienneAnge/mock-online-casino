package com.example.CasYnoRoyale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe qui permet de configurer la librairie Spring Security
 */
@Configuration
public class SecurityConfig {

    /**
     * Fonction qui crée un bean PasswordEncoder utilisant bcrypt (permet de hacher un mot de passe)
     * @return Le PasswordEncoder utilisant bcrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configurer les règles de sécurité HTTP
     * @param http          L'objet HttpSecurity à configurer
     * @return              La chaîne de filtres de sécurité configurée
     * @throws Exception    En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //Désactive la protection CSRF (permet de gérer les formulaires soi-même)
            .csrf(csrf -> csrf.disable()) 
            //Autorise toutes les requetes car géré par SessionInterceptor
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Explicite pour H2 (sinon innaccessible))
                .anyRequest().permitAll() 
            )

            //Autorise l'affiche des pages (sinon h2-console non affiché car externe à une iframe)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            //Désactive le formulaire par defaut de login de Spring
            .formLogin(form -> form.disable()) 
            //Désacrive le logout par defaut de Spring
            .logout(logout -> logout.disable()); 

        return http.build();
    }
}