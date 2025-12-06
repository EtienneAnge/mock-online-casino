package com.example.CasYnoRoyale;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Role;

import jakarta.servlet.http.HttpSession;

@Controller
public class SignupController {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/signup")
    public String signup(Model model) { 
        // Ajout d'un objet AppUser vide pour que Thymeleaf puisse construire le formulaire
        model.addAttribute("appUser", new AppUser());

        return "signup";
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute AppUser formUser,
            RedirectAttributes model,
            HttpSession session) {

        //Verification qu'il n'y ait pas de doublon
        //Recuperation de l'utilisateur avec le nom fourni
        AppUser existingUser = userRepository.findByUsername(formUser.getUsername());

        //L'utilisateur est recherché en bdd via son nom
        if (existingUser != null) {
            model.addFlashAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            return "redirect:/signup";
        }

        Role defaultRole = roleRepository.findByLabel("ROLE_USER"); 

        if (defaultRole == null) {
            System.out.println("ERREUR CRITIQUE: Le rôle 'ROLE_USER' n'est pas initialisé en base de données.");
            model.addFlashAttribute("error", "Erreur serveur: Rôle par défaut manquant.");
            return "redirect:/signup";
        }

        // Assigner le rôle trouvé à l'utilisateur
        formUser.setRole(defaultRole);

        formUser.setBalance(new BigDecimal(100)); // Initialiser le solde à 0
        formUser.setPassword(formUser.getPassword()); // mot de passe de l'utilisateur
        formUser.setName(formUser.getName()); //Nom de l'utilisateur

        //SAUVEGARDE EN BASE DE DONNÉES
        AppUser savedUser = userRepository.save(formUser);

        // CONNEXION MANUELLE (Crée la session HTTP simple)
        session.setAttribute("user", savedUser); 

        model.addFlashAttribute("message", "Bienvenue " + savedUser.getUsername() + " !");
        
        // Redirection vers l'accueil après auto-connexion
        return "redirect:/"; 
    }
} 
