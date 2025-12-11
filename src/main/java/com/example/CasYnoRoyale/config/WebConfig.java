package com.example.CasYnoRoyale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permet de definir les pages accessible avec ou sans authentification
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor; //Session Intercepteur qui verifie l'authentification de l'utilisateur

    /**
     * Ajoute les intercepteurs définis à la configuration Spring MVC
     * @param registry Le registre des intercepteurs
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionInterceptor)
                // Appliquer l'intercepteur à TOUTES les URLs (/**) pour les protéger
                .addPathPatterns("/**")

                // Exclusions (Pages Publiques)
                .excludePathPatterns(
                        "/login",
                        "/signup",
                        "/error",
                        "/");
    }
}