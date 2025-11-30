package com.example.CasYnoRoyale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.CasYnoRoyale.service.BlackjackService;

@Controller
@RequestMapping("/blackjack")
public class BlackjackController {

    private final BlackjackService blackjackService;

    @Autowired
    public BlackjackController(BlackjackService blackjackService) {
        this.blackjackService = blackjackService;
    }

    @GetMapping("")
    public String showGame(Model model) {
        model.addAttribute("bjService", blackjackService);
        return "blackjack";
    }

    @PostMapping("/hit")
    public String playerHit() {
        blackjackService.playerHit();
        return "redirect:/blackjack";
    }

    @PostMapping("/stand")
    public String playerStand() {
        blackjackService.playerStand();
        return "redirect:/blackjack";
    }

    @PostMapping("/newgame")
    public String newGame() {
        blackjackService.startNewGame();
        return "redirect:/blackjack";
    }
}