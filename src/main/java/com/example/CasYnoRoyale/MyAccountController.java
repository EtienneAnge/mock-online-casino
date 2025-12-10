package com.example.CasYnoRoyale;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

/**
 * Controller de la page MyAccount
 */
@Controller
public class MyAccountController {

    @Autowired
    AppUserRepository userRepository; //Acces aux donnees des utilisateurs

    @Autowired
    StatsService statsService; //Acces aux données des statistiques

    @Autowired
    PasswordEncoder passwordEncoder; //Pour hasher le mot de passe


    @GetMapping("/myaccount")
    public String myaccount() {
        return "myaccount";
    }

    /**
     * Mise à jour du nom de l'utilisateur
     * @param newName   Nouveau nom
     * @param model     Attributs de redirection
     * @param session   Session HTTP
     * @return          Redirection vers la page MyAccount
     */
    @PostMapping("/myaccount/update-name")
    public String updateName(@RequestParam("newName") String newName,
                            RedirectAttributes model,
                            HttpSession session) {

        //Recuperation de l'utilisateur en session
        AppUser user = (AppUser) session.getAttribute("user");
        
        //Au cas où l'utilisateur est null
        if (user == null) {
            return "redirect:/login";
        }

        //Mise à jour du nom
        user.setName(newName);

        //Sauvegarde en BDD
        userRepository.save(user);

        //Message de confirmation
        model.addFlashAttribute("message", "Nom mis à jour !");
        //Redirection
        return "redirect:/myaccount";
    }

    /**
     * Mise à jour du pseudo de l'utilisateur
     * @param newUsername   Nouveau pseudo
     * @param model         Attributs de redirection
     * @param session       Session HTTP
     * @return              Redirection vers la page MyAccount
     */
    @PostMapping("/myaccount/update-username")
    public String updateUsername(@RequestParam("newUsername") String newUsername,
                            RedirectAttributes model,
                            HttpSession session) {

        //Recuperation de l'utilisateur en session
        AppUser user = (AppUser) session.getAttribute("user");
        
        //Au cas où l'utilisateur est null
        if (user == null) {
            return "redirect:/login";
        }

        //Mise à jour du pseudo
        user.setUsername(newUsername);

        //Sauvegarde en BDD
        userRepository.save(user);

        //Message de confirmation
        model.addFlashAttribute("message", "Pseudo mis à jour !");
        //Redirection
        return "redirect:/myaccount";
    }


    /**
     * Mise à jour du mot de passe de l'utilisateur
     * @param newPassword   Nouveau mot de passe
     * @param oldPassword   Ancien mot de passe
     * @param model         Attributs de redirection
     * @param session       Session HTTP
     * @return              Redirection vers la page MyAccount
     */
    @PostMapping("/myaccount/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                            @RequestParam("oldPassword") String oldPassword,
                            RedirectAttributes model,
                            HttpSession session) {

        //Recuperation de l'utilisateur en session
        AppUser user = (AppUser) session.getAttribute("user");
        
        //Au cas où l'utilisateur est null
        if (user == null) {
            return "redirect:/login";
        }

        //Verifie si l'ancien mot de passe est correcte
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            //Message d'erreur
            model.addFlashAttribute("error", "Mot de passe invalide");
            //Redirection
            return "redirect:/myaccount";
        }

        //Mise à jour du mdp hashé en bdd
        user.setPassword(passwordEncoder.encode(newPassword));

        //Sauvegarde
        userRepository.save(user);

        //Message de confirmation
        model.addFlashAttribute("message", "Mot de passe mis à jour !");
        //Redirection
        return "redirect:/myaccount";
    }

    /**
     * Augmente le solde de 100€ avec une limiteà 1500
     * @param model         Attributs de redirection
     * @param session       Session HTTP
     * @return              Redirection vers la page MyAccount
     */
    @PostMapping("/myaccount/add-balance")
    public String addBalance(RedirectAttributes model,
                            HttpSession session) {
        //Variables
        //Recuperation de l'utilisateur en session
        AppUser user = (AppUser) session.getAttribute("user"); 
        BigDecimal maxBalance = new BigDecimal(1500.0);         //Valeur maximale du solde
        BigDecimal addingBalanceValue = new BigDecimal(100.0);  //Valeur ajoutée au solde à chaque clic sur le bouton
        BigDecimal newBalanceValue;                             //Nouvelle valeur du solde
        
        //Au cas où l'utilisateur est null
        if (user == null) {
            return "redirect:/login";
        }

        //Si le solde est egal ou supérieur à 1500
        if (user.getBalance().compareTo(maxBalance) >= 0) {
            //Message d'erreur
            model.addFlashAttribute("error", "Vous avez déjà 1500 de crédits. Dépensez les !");
        } else { //Sinon
            //Calcul du nouveau solde
            newBalanceValue = user.getBalance().add(addingBalanceValue);
            //Si le nouveau solde est supérieur à 1500
            if (newBalanceValue.compareTo(maxBalance) >= 0) {
                //Alors le nouveau solde vaut 1500
                user.setBalance(maxBalance);
            } else {
                //Mise à jour du solde
                user.setBalance(newBalanceValue);
            }

            //Sauvegarde
            userRepository.save(user);
            //Message de confirmation
            model.addFlashAttribute("message", "Solde mis à jour !");
        }

        //Redirection
        return "redirect:/myaccount";
    }


    /**
     * Acces aux transactions de l'utilisateur
     * @param session       Session HTTP
     * @param gameId        Id du jeux (permet de filtrer les données en fonction du jeu, facultatif)
     * @return              renvoie les données (transactions) au format json
     */
    @GetMapping("/api/stats/balance")
    @ResponseBody
    public ChartDataDTO getTransactions(HttpSession session,
                                        @RequestParam(required = false) Long gameId) {
        //Recuperation de l'utilisateur en session
        AppUser user = (AppUser) session.getAttribute("user");

        //Au cas où l'utilisateur est null
        if (user == null) {
            return null ;
        }
        
        //Renvoie les données
        return statsService.getEvolutionData(user, gameId);
    }
}
