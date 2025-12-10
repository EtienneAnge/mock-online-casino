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

@Component
public class AdminH2Filter implements Filter {
    // @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith("/h2-console")) {
            HttpSession session = req.getSession(false);
            AppUser user = (session != null) ? (AppUser) session.getAttribute("user") : null;

            boolean isAdmin = false;

            if (user == null) {
                res.sendRedirect("/");
                return;
            }

            if (user.getRole().getLabel().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }

            if (!isAdmin) {
                // Pas Admin ? On redirige vers l'accueil ou le login
                res.sendRedirect("/");
                return; // On arrête tout, la requête ne va pas plus loin
            }

        }

        // Si ce n'est pas h2-console, ou si c'est un Admin, on laisse passer
        chain.doFilter(request, response);
    }
}
