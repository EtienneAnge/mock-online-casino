package com.example.CasYnoRoyale.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.example.CasYnoRoyale.model.blackjack.Deck;
import com.example.CasYnoRoyale.model.blackjack.Hand;

@Service
@SessionScope 
public class BlackjackService {

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;

    
    private String gameState;
    private String gameMessage; 

    public BlackjackService() {
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.deck = new Deck(); 
        startNewGame();
    }

    public void startNewGame() {
        playerHand.clear();
        dealerHand.clear();
        if(deck == null) { deck = new Deck(); }

        gameState = "ONGOING";
        gameMessage = "Votre tour. Voulez-vous tirer ou rester ?";

        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        checkInitialBlackjack();
    }

    private void checkInitialBlackjack() {
        boolean playerBJ = playerHand.isBlackjack();
        boolean dealerBJ = dealerHand.isBlackjack();

        if (playerBJ && dealerBJ) {
            gameState = "PUSH";
            gameMessage = "Double Blackjack ! Égalité.";
        } else if (playerBJ) {
            gameState = "PLAYER_WIN";
            gameMessage = "Blackjack ! Vous avez gagné !";
        } else if (dealerBJ) {
            gameState = "DEALER_WIN";
            gameMessage = "Blackjack ! Le croupier gagne !";
        }
    }

    public void playerHit() {
        if (!gameState.equals("ONGOING")) return;

        playerHand.addCard(deck.drawCard());

        if (playerHand.isBusted()) {
            gameState = "DEALER_WIN";
            gameMessage = "Vous avez dépassé 21 (Bust) ! Le croupier gagne.";
        }
    }

    public void playerStand() {
        if (!gameState.equals("ONGOING")) return;
        dealerTurn();
    }

    private void dealerTurn() {
        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
        determineWinner();
    }

    private void determineWinner() {
        int playerScore = playerHand.calculateScore();
        int dealerScore = dealerHand.calculateScore();

        if (dealerHand.isBusted()) {
            gameState = "PLAYER_WIN";
            gameMessage = "Le croupier a dépassé 21 ! Vous avez gagné.";
        } else if (dealerScore > playerScore) {
            gameState = "DEALER_WIN";
            gameMessage = "Le croupier a un meilleur score. Vous avez perdu.";
        } else if (playerScore > dealerScore) {
            gameState = "PLAYER_WIN";
            gameMessage = "Vous avez un meilleur score ! Vous avez gagné.";
        } else {
            gameState = "PUSH";
            gameMessage = "Égalité (Push).";
        }
    }

    public Hand getPlayerHand() { return playerHand; }
    public Hand getDealerHand() { return dealerHand; }
    public String getGameState() { return gameState; }
    public String getGameMessage() { return gameMessage; }
    public boolean isGameOngoing() { return "ONGOING".equals(gameState); }
}