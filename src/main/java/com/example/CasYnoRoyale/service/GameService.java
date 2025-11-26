package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private Game roulette;
    private Game blackjack;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostConstruct
    public void initDatabaseOnStartup() {
        Game bj = new Game();
        blackjack = bj;
        bj.setLabel("BlackJack");
        bj.setUrl("/games/blackjack");
        
        gameRepository.save(bj);
        
        System.out.println("Jeu sauvegardé avec l'ID : " + bj.getIdGame());

        Game roulette = new Game();
        this.roulette = roulette;
        roulette.setLabel("Roulette");
        roulette.setUrl("/games/roulette");
        
        gameRepository.save(roulette);
        
        System.out.println("Jeu sauvegardé avec l'ID : " + roulette.getIdGame());
    }

    public Game getRoulette(){
        return roulette;
    }
}