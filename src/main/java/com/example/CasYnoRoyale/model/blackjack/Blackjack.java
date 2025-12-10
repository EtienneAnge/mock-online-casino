package com.example.CasYnoRoyale.model.blackjack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.CasYnoRoyale.database.AppUser;

public class Blackjack {

    private Deck deck;
    private final Hand dealerHand;
    private final List<Seat> seats = new ArrayList<>();
    private String gameStatus; 
    private long bettingEndTime; 
    private int currentPlayerIndex; 

    public Blackjack() {
        this.deck = new Deck();
        this.dealerHand = new Hand();
        startBettingPhase();
    }

    public List<Seat> getSeats() {
        return seats;
    }
    
    public String getStatus() {
        return gameStatus;
    }

    /**
     * Initialise ou réinitialise la phase de paris.
     * Vide la table et lance le chronomètre.
     */
    public final void startBettingPhase() {
        this.gameStatus = "BETTING";
        this.seats.clear();
        this.dealerHand.clear();
        this.bettingEndTime = System.currentTimeMillis() + 20000;
    }

    /**
     * Vérifie si le temps de mise est écoulé. Si oui, lance la partie.
     */
    public void checkTimer() {
        if ("BETTING".equals(gameStatus) && System.currentTimeMillis() > bettingEndTime) {
            startGame();
        }
    }

    public long getTimeRemaining() {
        if (!"BETTING".equals(gameStatus)) return 0;
        long remaining = (bettingEndTime - System.currentTimeMillis()) / 1000;
        return remaining < 0 ? 0 : remaining;
    }

    /**
     * Inscrit un utilisateur à la table avec sa mise.
     * Si l'utilisateur est déjà assis, sa mise est mise à jour.
     *
     * @param user   L'utilisateur (AppUser) qui souhaite placer une mise.
     * @param amount Le montant de la mise en jetons/argent.
     */
    public synchronized void placeBet(AppUser user, int amount) {
        if (!"BETTING".equals(gameStatus)) return;

        Optional<Seat> existingSeat = seats.stream().filter(s -> s.user.getIdUser().equals(user.getIdUser())).findFirst();
        
        if (existingSeat.isPresent()) {
            existingSeat.get().bet = amount;
        } else {
            Seat newSeat = new Seat(user, amount);
            seats.add(newSeat);
        }
    }

    /**
     * Lance la distribution des cartes et le premier tour.
     */
    private void startGame() {
        if (seats.isEmpty()) {
            startBettingPhase();
            return;
        }

        this.deck = new Deck();

        for (Seat seat : seats) {
            seat.hand.addCard(deck.drawCard());
            seat.hand.addCard(deck.drawCard());
        }
        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        this.gameStatus = "PLAYER_TURN";
        
        this.currentPlayerIndex = -1;
        nextTurn();
    }

    /**
     * Action : Le joueur demande une carte supplémentaire (Hit).
     *
     * @param user L'utilisateur qui effectue l'action (doit être le joueur actif).
     */
    public synchronized void hit(AppUser user) {
        Seat currentSeat = getCurrentSeat();
        if (currentSeat == null || !currentSeat.user.getIdUser().equals(user.getIdUser())) return;

        currentSeat.hand.addCard(deck.drawCard());

        if (currentSeat.hand.isBusted()) {
            currentSeat.status = "BUSTED";
            nextTurn(); 
        }
    }

    /**
     * Action : Le joueur s'arrête et garde sa main (Stand).
     *
     * @param user L'utilisateur qui effectue l'action (doit être le joueur actif).
     */
    public synchronized void stand(AppUser user) {
        Seat currentSeat = getCurrentSeat();
        if (currentSeat == null || !currentSeat.user.getIdUser().equals(user.getIdUser())) return;

        currentSeat.status = "STAND";
        nextTurn();
    }

    private Seat getCurrentSeat() {
        if (!"PLAYER_TURN".equals(gameStatus) || currentPlayerIndex >= seats.size()) return null;
        return seats.get(currentPlayerIndex);
    }

    /**
     * Passe la main au joueur suivant.
     * Gère récursivement les cas de Blackjack naturels pour sauter le tour du joueur.
     */
    private void nextTurn() {
        currentPlayerIndex++;
        
        if (currentPlayerIndex >= seats.size()) {
            startDealerTurn();
            return;
        }

        Seat currentSeat = seats.get(currentPlayerIndex);
        if (currentSeat.hand.isBlackjack()) {
            currentSeat.status = "BLACKJACK";
            nextTurn();
        }
    }

    private void startDealerTurn() {
        this.gameStatus = "DEALER_TURN";
        
        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
        
        resolveWinners();
    }

    /**
     * Calcule les vainqueurs et applique les gains.
     */
    private void resolveWinners() {
        this.gameStatus = "FINISHED";
        int dealerScore = dealerHand.calculateScore();
        boolean dealerBust = dealerHand.isBusted();
        boolean dealerBJ = dealerHand.isBlackjack();

        for (Seat seat : seats) {
            if ("BUSTED".equals(seat.status)) {
                seat.status = "LOST";
                continue;
            }

            int playerScore = seat.hand.calculateScore();
            double multiplier;
            boolean playerBJ = seat.hand.isBlackjack();

            if (playerBJ && dealerBJ) {
                seat.status = "PUSH";
                multiplier = 1.0;
            } else if (playerBJ) {
                seat.status = "WON BLACKJACK";
                multiplier = 2.5;
            } else if (dealerBJ) {
                seat.status = "LOST";
                multiplier = 0;
            } else if (dealerBust) {
                seat.status = "WON";
                multiplier = 2.0;
            } else if (playerScore > dealerScore) {
                seat.status = "WON";
                multiplier = 2.0;
            } else if (playerScore == dealerScore) {
                seat.status = "PUSH";
                multiplier = 1.0;
            } else {
                seat.status = "LOST";
                multiplier = 0;
            }

            if (multiplier > 0) {
                BigDecimal gain = BigDecimal.valueOf(seat.bet * multiplier);
                seat.user.setBalance(seat.user.getBalance().add(gain));
            }
        }
        
        this.bettingEndTime = System.currentTimeMillis() + 10000; 
    }
    
    /**
     * Vérifie si le temps d'affichage des résultats est écoulé. Si oui, relance une manche.
     */
    public void checkReset() {
        if ("FINISHED".equals(gameStatus) && System.currentTimeMillis() > bettingEndTime) {
            startBettingPhase();
        }
    }

    /**
     * Construit une Map représentant l'état complet du jeu pour l'envoi JSON.
     * 
     * @param requester L'utilisateur qui demande l'état (pour savoir qui est "moi").
     * @return Map structurée pour le frontend.
     */
    public Map<String, Object> getGameState(AppUser requester) {
        checkTimer(); 
        checkReset(); 

        Map<String, Object> data = new HashMap<>();
        data.put("status", this.gameStatus);
        data.put("timer", getTimeRemaining());
        
        // Info Croupier
        Map<String, Object> dealerData = new HashMap<>();
        switch (gameStatus) {
            case "BETTING" -> {
                dealerData.put("cards", Collections.emptyList());
                dealerData.put("score", 0);
            }
            case "PLAYER_TURN" -> {
                dealerData.put("cards", Collections.singletonList(dealerHand.getCards().get(0)));
                dealerData.put("score", "?");
            }
            default -> {
                dealerData.put("cards", dealerHand.getCards());
                dealerData.put("score", dealerHand.calculateScore());
            }
        }
        data.put("dealer", dealerData);

        // Liste des joueurs
        List<Map<String, Object>> playersData = new ArrayList<>();
        boolean isRequesterTurn = false;

        for (int i = 0; i < seats.size(); i++) {
            Seat s = seats.get(i);
            Map<String, Object> pInfo = new HashMap<>();
            pInfo.put("name", s.user.getName());
            pInfo.put("cards", s.hand.getCards());
            pInfo.put("score", s.hand.calculateScore());
            pInfo.put("bet", s.bet);
            pInfo.put("status", s.status);
            
            boolean isCurrentTurn = ("PLAYER_TURN".equals(gameStatus) && i == currentPlayerIndex);
            pInfo.put("isTurn", isCurrentTurn);

            if (s.user.getIdUser().equals(requester.getIdUser())) {
                pInfo.put("isMe", true);
                if (isCurrentTurn) isRequesterTurn = true;
            } else {
                pInfo.put("isMe", false);
            }
            playersData.add(pInfo);
        }
        data.put("players", playersData);
        data.put("isMyTurn", isRequesterTurn);

        return data;
    }
}