package com.example.CasYnoRoyale.model.blackjack; // Déplacé dans le package model

import java.math.BigDecimal;

import com.example.CasYnoRoyale.database.AppUser;

import java.util.HashMap;
import java.util.Map;

// Plus d'annotations @Service ou @SessionScope
public class Blackjack {

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    
    // On garde la trace du joueur actuel pour les gains
    private AppUser currentUser;

    private String gameState; // "BETTING", "ONGOING", "PLAYER_WIN", etc.
    private String gameMessage;
    private int currentBet = 0;

    public Blackjack() {
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.deck = new Deck();
        resetToBettingPhase();
    }

    // 1. Initialise la phase de pari
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

    // 2. Méthode appelée par le Controller : reçoit le User et le montant
    public void placeBet(AppUser user, int amount) {
        this.currentUser = user; // On lie le jeu à l'utilisateur
        
        // La vérification du solde se fait idéalement dans le Controller ou ici
        // Ici on suppose que le Controller a vérifié user.getBalance() >= amount
        
        this.currentBet = amount;
        
        // DÉBITER LE JOUEUR
        // Attention : adaptez selon que balance est BigDecimal, Double ou Int dans AppUser
        // Exemple si c'est BigDecimal : user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(amount)));
        // Exemple si c'est Double/Int :
        BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(amount)); 
        user.setBalance((BigDecimal) newBalance); // Cast selon votre type exact dans AppUser

        dealInitialCards();
    }

    private void dealInitialCards() {
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
        // Le croupier tire tant qu'il a moins de 17 (Logique standard)
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
        this.gameState = state;
        this.gameMessage = message;
        
        if (payoutMultiplier > 0 && currentUser != null) {
            double gain = currentBet * payoutMultiplier;
            // CRÉDITER LE JOUEUR
            // Adapter selon le type (BigDecimal/Long/Double)
            BigDecimal newBalance = currentUser.getBalance().add(BigDecimal.valueOf(gain));
            currentUser.setBalance(newBalance);
        }
        // Pas besoin de "resetToBettingPhase" ici, le frontend demandera une nouvelle partie
    }

    // --- MÉTHODE IMPORTANTE POUR L'API JSON ---
    // Cette méthode formate les données pour que le Controller les envoie au Frontend
    public Map<String, Object> getGameState() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("status", this.gameState);
        data.put("message", this.gameMessage);
        
        // Cartes du joueur
        // Supposons que hand.getCards() renvoie une List<Card>
        data.put("playerCards", playerHand.getCards()); 
        data.put("playerValue", playerHand.calculateScore());

        // Cartes du croupier
        if (gameState.equals("ONGOING")) {
            // Pendant le jeu, on ne montre que la première carte du croupier (ou la 2eme cachée)
            // Ici on envoie la liste, le frontend peut choisir de n'afficher que la première
            // Ou on masque côté serveur :
            data.put("dealerCards", java.util.Collections.singletonList(dealerHand.getCards().get(0)));
            data.put("dealerValue", "?");
        } else {
            // Partie finie : on montre tout
            data.put("dealerCards", dealerHand.getCards());
            data.put("dealerValue", dealerHand.calculateScore());
        }

        return data;
    }
}