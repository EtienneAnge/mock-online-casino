package com.example.CasYnoRoyale.config;
/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //Definition du passwordEncoder utilisé pour hacher les mots de passe 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // 1. On lui donne ton service pour trouver l'utilisateur
        authProvider.setUserDetailsService(userDetailsService);
        // 2. On lui donne l'encodeur pour vérifier le mot de passe
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. DÉSACTIVER CSRF et IFRAMES pour H2 (C'est ce qui est déjà fait)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // 2. AUTORISER LES CHEMINS PUBLICS (Accueil, Connexion, Inscription, H2)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/h2-console/**",
                                "/login", // VOTRE PAGE DE CONNEXION
                                "/signup" // VOTRE PAGE D'INSCRIPTION
                        ).permitAll() // CES PAGES SONT ACCESSIBLES À TOUS

                        .anyRequest().authenticated() // TOUT LE RESTE DOIT ÊTRE AUTHENTIFIÉ
                )

                // 3. ACTIVER LE FORMULAIRE DE CONNEXION (LIGNE MANQUANTE)
                .formLogin(form -> form
                        // Spécifie la page de connexion personnalisée (par défaut, c'est /login)
                        .loginPage("/login")
                        // URL où le formulaire POST sera soumis (par défaut, c'est /login)
                        .loginProcessingUrl("/login")
                        // Page de redirection après une connexion réussie
                        .defaultSuccessUrl("/", true)
                        // Page de redirection après un échec
                        .failureUrl("/login?error=true")
                        .permitAll() // Le formulaire de connexion est toujours public
                )

                // 4. ACTIVER LA DÉCONNEXION (Bonne pratique)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
*/