package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.repository.GameRepository;

import com.example.CasYnoRoyale.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final AppUserRepository appUserRepository;
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    @Getter
    private Game roulette;
    private Game blackjack;

    public GameService(GameRepository gameRepository, RoomRepository roomRepository, AppUserRepository appUserRepository) {
        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.appUserRepository= appUserRepository;
    }

    @PostConstruct
    @Transactional
    public void initDatabaseOnStartup() {
        appUserRepository.deleteAll();
        roomRepository.deleteAll();
        gameRepository.deleteAll();
        Game bj = gameRepository.findByLabel("BlackJack");
        if (bj == null) {
            bj = new Game();
            blackjack = bj;
            bj.setLabel("BlackJack");
            bj.setUrl("/games/blackjack");
            gameRepository.save(bj);

        }
        Game roulette = gameRepository.findByLabel("Roulette");
        if (roulette == null) {
            roulette = new Game();
            this.roulette = roulette;
            roulette.setLabel("Roulette");
            roulette.setUrl("/games/roulette");
            gameRepository.save(roulette);

        }


    }

}