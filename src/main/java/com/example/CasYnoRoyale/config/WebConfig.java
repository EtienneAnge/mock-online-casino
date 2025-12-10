package com.example.CasYnoRoyale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionInterceptor)
                // Appliquer l'intercepteur à TOUTES les URLs (/**) pour les protéger
                .addPathPatterns("/**")

                // Exclusions (Pages Publiques) ---
                .excludePathPatterns(
                        "/login",
                        "/signup",
                        "/error",
                        "/");
    }
}