package com.example.CasYnoRoyale.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Récupérer la session existante (false = ne pas en créer une si elle n'existe pas)
        HttpSession session = request.getSession(false); 
        System.out.println(session);
        // Vérifie si l'utilisateur est authentifié : la session existe ET l'attribut "user" est présent
        boolean isAuthenticated = (session != null && session.getAttribute("user") != null);

        // Si l'utilisateur n'est PAS authentifié (n'a pas l'objet "user" en session)
        if (!isAuthenticated) {
            
            System.out.println("Interception : Non connecté. Redirection vers /login.");
            
            // Effectue la redirection vers la page de connexion
            // request.getContextPath() garantit que le chemin absolu est correct (ex: /CasYnoRoyale/login)
            response.sendRedirect(request.getContextPath() + "/login");
            
            // Retourne false pour arrêter l'exécution du contrôleur initialement ciblé
            return false;
        }

        // Si l'utilisateur est authentifié (objet "user" trouvé), on laisse la requête continuer
        return true;
    }
}