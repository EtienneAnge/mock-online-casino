package com.example.CasYnoRoyale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.CasYnoRoyale.database.AppUser;

@Controller
public class HomeController {
    @GetMapping("/")
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

}
