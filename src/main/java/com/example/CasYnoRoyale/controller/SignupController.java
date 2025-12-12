package com.example.CasYnoRoyale.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

/**
 * Controller de la page d'inscription
 */
@Controller
public class SignupController {

    @Autowired
    AppUserRepository userRepository;   //Le repository des utilisateurs

    @Autowired
    RoleRepository roleRepository;      //Le repository des roles

    @Autowired
    PasswordEncoder passwordEncoder;    //hasher de mot de passe

    /**
     * page d'inscription
     * @param model Les attributs de redirection
     * @return fichier à ouvrir
     */
    @GetMapping("/signup")
    public String signup(Model model) { 
        //Ajout d'un objet AppUser vide pour que Thymeleaf puisse construire le formulaire
        model.addAttribute("appUser", new AppUser());

        return "signup";
    }

    /**
     * Gestion du formulaire d'inscription
     * @param formUser  L'utilisateur soumis via le formulaire
     * @param model     Les attributs de redirection
     * @param session   La session HTTP
     * @return          La redirection vers la page d'accueil ou d'inscription en cas d'erreur
     */
    @PostMapping("/signup")
    public String postSignup(@ModelAttribute AppUser formUser,
            RedirectAttributes model,
            HttpSession session) {

        //Recuperation de l'utilisateur avec le nom fourni (verifie qu'il n'y ai pas de doublon)
        AppUser existingUser = userRepository.findByUsername(formUser.getUsername());

        //Si l'utilisateur existe deja
        if (existingUser != null) {
            //Message d'erreur
            model.addFlashAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            //Redirection
            return "redirect:/signup";
        }

        //Initialisation du role USER
        Role defaultRole = roleRepository.findByLabel("ROLE_USER"); 

        //Si le role n'existe pas en bdd
        if (defaultRole == null) {
            //Message d'erreur
            model.addFlashAttribute("error", "Erreur serveur: Rôle par défaut manquant.");
            //redirection
            return "redirect:/signup";
        }

        //Initialisation des attributs du nouveau compte
        formUser.setRole(defaultRole);                                          //Role USER
        formUser.setBalance(new BigDecimal(0));                                 //Solde à 0
        formUser.setPassword(passwordEncoder.encode(formUser.getPassword()));   //Mot de passe hashé
        formUser.setName(formUser.getName());                                   //Nom de l'utilisateur
        formUser.setUsername(formUser.getUsername());                           //Pseudo de l'utilisateur

        //Sauvegarde en bdd et dans l'utilisateur de la session
        AppUser savedUser = userRepository.save(formUser);

        //Attribut l'utilisateur à la session
        session.setAttribute("user", savedUser); 

        //Message de bienvenue
        model.addFlashAttribute("message", "Bienvenue " + savedUser.getUsername() + " !");
        
        //Redirection
        return "redirect:/"; 
    }
} 
