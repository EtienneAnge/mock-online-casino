package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

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
    @Transactional
    public void initDatabaseOnStartup() {
        //gameRepository.deleteAll();
        Game bj = gameRepository.findByLabel("Blackjack");
        if(bj == null){
             bj = new Game();
            blackjack = bj;
            bj.setLabel("Blackjack");
            bj.setUrl("/games/blackjack");
            gameRepository.save(bj);
        
        }
        
        
        Game roulette = gameRepository.findByLabel("Roulette");
         if(roulette == null){
            roulette = new Game();
        this.roulette = roulette;
        roulette.setLabel("Roulette");
        roulette.setUrl("/games/roulette");
                gameRepository.save(roulette);

         }
        
        
    }

    public Game getRoulette(){
        return roulette;
    }
}