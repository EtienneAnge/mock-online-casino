package com.example.CasYnoRoyale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;

import com.example.CasYnoRoyale.database.AppUser;

/**
 * Controller de la page de connexion
 */
@Controller
public class LoginController {

    @Autowired
    AppUserRepository userRepository;   //Le repository des utilisateurs

    @Autowired
    PasswordEncoder passwordEncoder;    //Hasher de mot de passe

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Gestion du formulaire de connexion
     * @param formUser  L'utilisateur soumis via le formulaire
     * @param model     Les attributs de redirection
     * @param session   La session HTTP
     * @return          La redirection vers la page d'accueil ou de connexion en cas d'erreur
     */
    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpSession session) {

        //Recuperation de l'utilisateur en bdd via le nom fourni
        AppUser user = userRepository.findByUsername(
                formUser.getUsername()
        );

        //Verification de l'existence de l'utilisateur
        if (user == null) {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }


        //Verification du mot de passe
        //S'il est correcte
        if (passwordEncoder.matches(formUser.getPassword(),user.getPassword())){
            //Attribution du user a la session
            session.setAttribute("user", user);
            //Message de bienvenue
            model.addFlashAttribute("message", "Bienvenue " + user.getUsername());
            //redirection à l'accueil
            return "redirect:/";
        } else { //Sinon
            //Message d'erreur
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            //Redirection à la page de connexion
            return "redirect:/login";
        }
    }
}