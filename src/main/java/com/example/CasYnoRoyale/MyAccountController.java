package com.example.CasYnoRoyale;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.model.ChartDataDTO;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.service.StatsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MyAccountController {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    StatsService statsService;

    @GetMapping("/myaccount")
    public String myaccount() {
        return "myaccount";
    }


    @PostMapping("/myaccount/update-name")
    public String updateName(@RequestParam("newName") String newName,
                            RedirectAttributes model,
                            HttpSession session) {

        AppUser user = (AppUser) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        user.setName(newName);

        userRepository.save(user);

        model.addFlashAttribute("message", "Nom mis à jour !");
        return "redirect:/myaccount";
    }

    @PostMapping("/myaccount/update-username")
    public String updateUsername(@RequestParam("newUsername") String newUsername,
                            RedirectAttributes model,
                            HttpSession session) {

        AppUser user = (AppUser) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        user.setUsername(newUsername);

        userRepository.save(user);

        model.addFlashAttribute("message", "Pseudo mis à jour !");
        return "redirect:/myaccount";
    }

    @PostMapping("/myaccount/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                            @RequestParam("oldPassword") String oldPassword,
                            RedirectAttributes model,
                            HttpSession session) {

        AppUser user = (AppUser) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        if (!user.getPassword().equals(oldPassword)) {
            model.addFlashAttribute("error", "Mot de passe invalide");
            return "redirect:/myaccount";
        }

        user.setPassword(newPassword);

        userRepository.save(user);

        model.addFlashAttribute("message", "Mot de passe mis à jour !");
        return "redirect:/myaccount";
    }

    @PostMapping("/myaccount/add-balance")
    public String addBalance(RedirectAttributes model,
                            HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        BigDecimal maxBalance = new BigDecimal(1500.0);
        BigDecimal addingBalanceValue = new BigDecimal(100.0);
        BigDecimal newBalanceValue;
        
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getBalance().compareTo(maxBalance) >= 0) {
            model.addFlashAttribute("error", "Vous avez déjà 1500 de crédits. Dépensez les !");
        } else {
            newBalanceValue = user.getBalance().add(addingBalanceValue);
            if (newBalanceValue.compareTo(maxBalance) >= 0) {
                user.setBalance(maxBalance);
            } else {
                user.setBalance(newBalanceValue);
            }

            userRepository.save(user);
            model.addFlashAttribute("message", "Solde mis à jour !");
        }

        return "redirect:/myaccount";
    }



    @GetMapping("/api/stats/balance")
    @ResponseBody
    public ChartDataDTO getTransactions(HttpSession session,
                                        @RequestParam(required = false) Long gameId) {
        AppUser user = (AppUser) session.getAttribute("user");

        if (user == null) {
            return null ;
        }
        System.out.println("C'est ok");
        return statsService.getEvolutvoidionData(user, gameId);

    }
}
