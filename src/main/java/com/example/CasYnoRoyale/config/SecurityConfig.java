package com.example.CasYnoRoyale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //Definition du passwordEncoder utilisé pour hacher les mots de passe 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Definition du filtre de sécurité HTTP (règles d'acces)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/login", "/signup").permitAll() //Pages accessibles sans authentification
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")                             //Page de connexion personnalisée
            .loginProcessingUrl("/login")           //URL de traitement du formulaire de connexion
            .defaultSuccessUrl("/", true) //Redirection après connexion réussie
            .permitAll() 
        )
        .logout(logout -> logout
            .logoutUrl("/logout") //redirection lors de la deconnexion
            .permitAll()
        );

        return http.build();
    }
}
