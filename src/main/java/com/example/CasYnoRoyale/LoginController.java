package com.example.CasYnoRoyale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.CasYnoRoyale.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import com.example.CasYnoRoyale.database.AppUser;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpSession session) {

        System.out.println("testt");
        AppUser user = userRepository.findByUsername(
                formUser.getUsername()
        );


        if (user == null) {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            System.out.println("Utilisateur non trouvé : " + formUser.getUsername());
            return "redirect:/login";
        }


        System.out.println(formUser.getPassword());
        System.out.println(user.getPassword());
        if (formUser.getPassword().equals(user.getPassword())){
            System.out.println("ok");
            session.setAttribute("user", user);
            model.addFlashAttribute("message", "Bienvenue " + user.getUsername());
            System.out.println("Utilisateur connecté : " + user.getUsername());
            return "redirect:/";
        } else {
            System.out.println("pas ok ");
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }
    }
}