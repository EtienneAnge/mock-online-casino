package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
public class GameController {
    final RoomService roomService;
    final GameRepository gameRepository;
    public GameController(RoomService roomService, GameRepository gameRepository) {
        this.roomService = roomService;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/games")
    public String redirectRoom(
            @RequestParam("idRoom") String idRoom,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            Room r = roomService.joinRoom(idRoom, user, null);
            if(r == null) {
                throw new IllegalArgumentException("Impossible de créer une salle sans jeu spécifié.");
            }

            String gameLabel = r.getGame().getLabel();
            return "redirect:/games/" + gameLabel.toLowerCase() + "?idRoom=" + idRoom;

        } catch (NoSuchElementException | IllegalArgumentException e) {
            String leMessageErreur = e.getMessage();
            System.out.println("Erreur attrapée : " + leMessageErreur);
            redirectAttributes.addFlashAttribute("error", leMessageErreur);

            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Code incorrect.");
            return "redirect:/";
        }
    }
}
