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

    // NOUVEAUX ATTRIBUTS
    private int balance = 1000; // Solde de départ (exemple)
    private int currentBet = 0; // Mise de la manche en cours

    public BlackjackService() {
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.deck = new Deck();
        // Au démarrage, on demande de miser
        resetToBettingPhase();
    }

    // 1. Initialise la phase de pari (ne distribue PAS encore les cartes)
    public void startNewGame() {
        resetToBettingPhase();
    }

    private void resetToBettingPhase() {
        gameState = "BETTING";
        gameMessage = "Veuillez placer votre mise pour commencer.";
        currentBet = 0;
        playerHand.clear();
        dealerHand.clear();
    }

    // 2. Méthode appelée quand le joueur valide sa mise
    public void placeBet(int amount) {
        if (amount <= 0 || amount > balance) {
            gameMessage = "Mise invalide (Solde insuffisant ou montant nul).";
            return;
        }

        this.currentBet = amount;
        this.balance -= amount; // On déduit la mise tout de suite
        
        // On lance la distribution
        dealInitialCards();
    }

    // Anciennement startNewGame, maintenant privé et appelé après la mise
    private void dealInitialCards() {
        if(deck == null) { deck = new Deck(); } // Sécurité deck vide

        gameState = "ONGOING";
        gameMessage = "Mise de " + currentBet + "€. Votre tour.";

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
            processEndGame("PUSH", "Double Blackjack ! Égalité.", 1.0);
        } else if (playerBJ) {
            // Blackjack paie 3 pour 2 (2.5x la mise totale récupérée)
            processEndGame("PLAYER_WIN", "Blackjack ! Vous gagnez 1.5x la mise !", 2.5);
        } else if (dealerBJ) {
            processEndGame("DEALER_WIN", "Blackjack du croupier ! Vous perdez.", 0);
        }
    }

    public void playerHit() {
        if (!gameState.equals("ONGOING")) return;
        playerHand.addCard(deck.drawCard());
        if (playerHand.isBusted()) {
            processEndGame("DEALER_WIN", "Vous avez sauté (Bust) !", 0);
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
            processEndGame("PLAYER_WIN", "Croupier saute ! Vous gagnez.", 2.0);
        } else if (dealerScore > playerScore) {
            processEndGame("DEALER_WIN", "Le croupier gagne.", 0);
        } else if (playerScore > dealerScore) {
            processEndGame("PLAYER_WIN", "Vous gagnez !", 2.0);
        } else {
            processEndGame("PUSH", "Égalité (Push). Mise remboursée.", 1.0);
        }
    }

    // Gestion centralisée de la fin de partie et des gains
    private void processEndGame(String state, String message, double payoutMultiplier) {
        gameState = state;
        gameMessage = message;
        
        if (payoutMultiplier > 0) {
            int gain = (int) (currentBet * payoutMultiplier);
            this.balance += gain;
        }
    }

    // Getters pour la vue
    public Hand getPlayerHand() { return playerHand; }
    public Hand getDealerHand() { return dealerHand; }
    public String getGameState() { return gameState; }
    public String getGameMessage() { return gameMessage; }
    public boolean isGameOngoing() { return "ONGOING".equals(gameState); }
    public boolean isBettingPhase() { return "BETTING".equals(gameState); } // Pour afficher le formulaire
    public int getBalance() { return balance; }
    public int getCurrentBet() { return currentBet; }
}