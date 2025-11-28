package com.example.CasYnoRoyale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    public String index(HttpServletRequest request, Model model){
        AppUser user = (AppUser)request.getSession().getAttribute("user");
        if(user!=null) {
            System.out.println(user.toString());
        }
        if(user==null){
            System.out.println("no user");
        }else{
            System.out.println("session user:" + user.getUsername());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpServletRequest request) {

        AppUSer user = userRepository.findUserByUsername(
                formUser.getUsername()
        );


        if (user == null) {
            model.addFlashAttribute("error", "Nom d'utilisateur ou mdp invalide");
            return "redirect:/login";
        }

    }
}
