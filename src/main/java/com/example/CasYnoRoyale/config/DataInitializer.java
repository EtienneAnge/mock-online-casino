package com.example.CasYnoRoyale.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.repository.UserRepository;
import com.example.CasYnoRoyale.database.AppUser; // Assurez-vous que les imports sont corrects

@Configuration
public class DataInitializer {

    // Cette méthode s'exécute juste après le démarrage du contexte Spring
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository, UserRepository userRepository) {

        // Le corps de la fonction à exécuter au démarrage
        return args -> {

            // Initialisation des rôles

            // Role User
            if (roleRepository.findByLabel("ROLE_USER") == null) {
                Role userRole = new Role();
                userRole.setLabel("ROLE_USER");
                userRole.setLevel(0);
                roleRepository.save(userRole);
                System.out.println("Rôle 'ROLE_USER' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_USER' déjà existant.");
            }

            // Role Admin
            if (roleRepository.findByLabel("ROLE_ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setLabel("ROLE_ADMIN");
                adminRole.setLevel(1);
                roleRepository.save(adminRole);
                System.out.println("Rôle 'ROLE_ADMIN' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_ADMIN' déjà existant.");
            }

            // Initialisation compte admin
            // Role Admin
            if (userRepository.findByUsername("admin") == null) {
                AppUser adminUser = new AppUser();

                Role adminRoleForUser = roleRepository.findByLabel("ROLE_ADMIN");

                if (adminRoleForUser == null) {
                    System.out.println("ERREUR CRITIQUE: Le rôle 'ROLE_USER' n'est pas initialisé en base de données.");
                }

                adminUser.setBalance(BigDecimal.ZERO);
                adminUser.setRole(adminRoleForUser);
                adminUser.setName("admin");
                adminUser.setUsername("admin");
                adminUser.setPassword("admin");

                userRepository.save(adminUser);
              
                System.out.println("User 'admin' créé et inséré en BDD.");
            } else {
                System.out.println("User 'admin' déjà existant.");
            }

        };
    }
}