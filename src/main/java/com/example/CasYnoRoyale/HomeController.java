package com.example.CasYnoRoyale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    /*@GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute AppUser formUser,
                            RedirectAttributes model,
                            HttpServletRequest request) {

        AppUSer user = userRepository.findByUsernameAndPassword(
                formUser.getUsername(),
                formUser.getPassword()
        );
    }*/
}
