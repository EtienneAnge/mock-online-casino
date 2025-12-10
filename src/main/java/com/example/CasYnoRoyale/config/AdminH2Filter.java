package com.example.CasYnoRoyale.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.example.CasYnoRoyale.database.AppUser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
Classe qui permet de filtrer l'accès à la console h2 aux seuls utilisateurs Admin
*/
@Component
public class AdminH2Filter implements Filter {
    /**
    * Filtre les requêtes pour restreindre l'accès à la console H2 aux seuls utilisateurs administrateurs.
    * @param request    Requete entrante
    * @param response   Reponse sortante
    * @param chain      Chaine de filtres
    */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        //Variables
        //Requete et réponse HTTP
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //Booleen pour savoir si le user est Admin
        boolean isAdmin = false;

        //Si la requete demande d'aller à h2-console
        if (req.getRequestURI().startsWith("/h2-console")) {
            //Recuperation de la session et de l'utilisateur en session (false signifie que si la session n'existe pas, on ne la crée pas)
            HttpSession session = req.getSession(false);
            //Si la session n'existe pas, user sera null
            AppUser user = (session != null) ? (AppUser) session.getAttribute("user") : null;

            //Si l'utilisateur n'est pas connecté, on le redirige vers l'accueil
            if (user == null) {
                res.sendRedirect("/");
                return;
            }

            //Vérification du rôle Admin
            if (user.getRole().getLabel().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }

            //Si l'utilisateur n'est pas Admin
            if (!isAdmin) {
                //Il est redirigé vers l'accueil
                res.sendRedirect("/");
                return;
            }
        }

        //Si c'est un admin ou que la requete ne concerne pas h2-console, on laisse passer la requete
        chain.doFilter(request, response);
    }
}
