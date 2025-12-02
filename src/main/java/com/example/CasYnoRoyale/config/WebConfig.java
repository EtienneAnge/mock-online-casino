package com.example.CasYnoRoyale.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new SessionInterceptor())

                // Appliquer l'intercepteur à TOUTES les URLs (/**) pour les protéger
                .addPathPatterns("/**")

                // Exclusions (Pages Publiques) ---
                .excludePathPatterns(
                        "/login",
                        "/signup",
                        "/h2-console/**",
                        "/");

    }
}