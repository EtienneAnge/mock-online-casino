package com.example.CasYnoRoyale;

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

@Controller
public class LoginController {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpSession session) {

        AppUser user = userRepository.findByUsername(
                formUser.getUsername()
        );


        if (user == null) {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }


        System.out.println(passwordEncoder.encode(formUser.getPassword()));
        System.out.println(user.getPassword());
        if (passwordEncoder.matches(formUser.getPassword(),user.getPassword())){
            session.setAttribute("user", user);
            model.addFlashAttribute("message", "Bienvenue " + user.getUsername());
            return "redirect:/";
        } else {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }
    }
}