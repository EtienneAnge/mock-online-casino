package com.example.CasYnoRoyale.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepteur de session permettant de limiter l'accès à certaines page en fonction de l'authentification de l'utilisateur.
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * Intercepte les requêtes entrantes avant qu'elles n'atteignent le contrôleur.
     * @param request   Requête HTTP entrante
     * @param response  Réponse HTTP sortante
     * @param handler   Gestionnaire de la requête
     * @return          true si la requête doit continuer vers le contrôleur, false pour l'arreter
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //Recupère la session (false signifie que si la session n'existe pas, on ne la crée pas)
        HttpSession session = request.getSession(false); 
        //Verifie si l'utilisateur est authentifié
        boolean isAuthenticated = (session != null && session.getAttribute("user") != null);

        //Si l'utilisateur n'est pas connecté
        if (!isAuthenticated) {
            //Redirection vers le login
            response.sendRedirect(request.getContextPath() + "/login");
        
            return false;
        }

        return true;
    }
}