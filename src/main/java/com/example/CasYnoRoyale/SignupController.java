package com.example.CasYnoRoyale;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.example.CasYnoRoyale.repository.UserRepository;
import com.example.CasYnoRoyale.database.AppUser;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SignupController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SignupController(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signup(Model model) { 
        // Ajout d'un objet AppUser vide pour que Thymeleaf puisse construire le formulaire
        model.addAttribute("appUser", new AppUser());

        return "signup";
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute AppUser formUser,
            RedirectAttributes model,
            HttpServletRequest request) {

        //Verification qu'il n'y ait pas de doublon
        //Recuperation de l'utilisateur avec le nom fourni
        AppUser existingUser = userRepository.findUserByUsername(formUser.getUsername());

        //L'utilisateur est recherché en bdd via son nom
        if (existingUser != null) {
            model.addFlashAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            return "redirect:/signup";
        }

        String hashedPassword = passwordEncoder.encode(formUser.getPassword());
        formUser.setPassword(hashedPassword);

        AppUser savedUser = userRepository.save(formUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            savedUser.getUsername(), 
            //savedUser.getPassword(),
            null,
            savedUser.getAuthorities()
        );

        // Lier le jeton à la requête actuelle
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Placer le jeton dans le contexte de sécurité (authentification réussie)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // --- FIN AUTO-CONNEXION ---

        model.addFlashAttribute("message", "Bienvenue " + savedUser.getUsername() + ", votre compte est prêt !");
        return "redirect:/"; // Redirection vers la page d'accueil
    }
}
