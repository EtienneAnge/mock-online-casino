package com.example.CasYnoRoyale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    //Index
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model){
        return "index";
    }

    //logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    //error
    @GetMapping("/error")
    public String error(HttpSession session) {
        session.invalidate();
        return "error";
    }
}
