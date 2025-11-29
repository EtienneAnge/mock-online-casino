package com.example.CasYnoRoyale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.CasYnoRoyale.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import com.example.CasYnoRoyale.database.AppUser;

@Controller
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpServletRequest request) {

        AppUser user = userRepository.findUserByUsername(
                formUser.getUsername()
        );


        if (user == null) {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }

        if (passwordEncoder.matches(formUser.getPassword(), user.getPassword())){
            request.getSession().setAttribute("user", user);
            model.addFlashAttribute("message", "Bienvenue " + user.getUsername());
            return "redirect:/";
        } else {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }

    }
}