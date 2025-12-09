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

    public static class Seat {
        public AppUser user;
        public Hand hand;
        public int bet;
        public String status; // "WAITING", "PLAYING", "BUSTED", "STAND", "WON", "LOST", "BLACKJACK"
        public BigDecimal initialBalance;

        public Seat(AppUser user, int bet) {
            this.user = user;
            this.bet = bet;
            this.hand = new Hand();
            this.status = "PLAYING";
            this.initialBalance = user.getBalance();
        }
    }

    private Deck deck;
    private Hand dealerHand;
    private List<Seat> seats = new ArrayList<>();
    
    private String gameStatus; 
    private long bettingEndTime; 
    private int currentPlayerIndex; 

    public Blackjack() {
        this.deck = new Deck();
        this.dealerHand = new Hand();
        startBettingPhase();
    }

    // --- ACCESSEUR POUR LE CONTROLEUR ---
    // Nécessaire pour sauvegarder les utilisateurs en BDD à la fin
    public List<Seat> getSeats() {
        return seats;
    }
    
    public String getStatus() {
        return gameStatus;
    }

    // --- LOGIQUE DU TIMER ET DES PHASES ---

    public void startBettingPhase() {
        this.gameStatus = "BETTING";
        this.seats.clear();
        this.dealerHand.clear();
        this.bettingEndTime = System.currentTimeMillis() + 20000;
    }

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

    // --- ACTIONS DES JOUEURS ---

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

    private void startGame() {
        if (seats.isEmpty()) {
            startBettingPhase();
            return;
        }

        // On crée un nouveau paquet et on mélange
        this.deck = new Deck();
        //this.deck.shuffle();

        // Distribution initiale
        for (Seat seat : seats) {
            seat.hand.addCard(deck.drawCard());
            seat.hand.addCard(deck.drawCard());
        }
        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        this.gameStatus = "PLAYER_TURN";
        
        // CORRECTION 2 : On initialise l'index à -1 et on appelle nextTurn()
        // Cela permet de vérifier immédiatement si le 1er joueur a un Blackjack
        this.currentPlayerIndex = -1;
        nextTurn();
    }

    public synchronized void hit(AppUser user) {
        Seat currentSeat = getCurrentSeat();
        if (currentSeat == null || !currentSeat.user.getIdUser().equals(user.getIdUser())) return;

        currentSeat.hand.addCard(deck.drawCard());

        if (currentSeat.hand.isBusted()) {
            currentSeat.status = "BUSTED";
            nextTurn(); 
        }
    }

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

    // CORRECTION 2 : Logique récursive pour sauter les tours si Blackjack
    private void nextTurn() {
        currentPlayerIndex++;
        
        // Si on a dépassé le dernier joueur, c'est au croupier
        if (currentPlayerIndex >= seats.size()) {
            startDealerTurn();
            return;
        }

        // Vérification automatique du Blackjack pour le nouveau joueur actif
        Seat currentSeat = seats.get(currentPlayerIndex);
        if (currentSeat.hand.isBlackjack()) {
            // Le joueur a un Blackjack naturel !
            currentSeat.status = "BLACKJACK"; // Statut spécial (attente résultat)
            // On passe immédiatement au joueur suivant (récursion)
            nextTurn();
        }
        // Sinon, c'est bien son tour, on reste ici (status reste "PLAYING")
    }

    private void startDealerTurn() {
        this.gameStatus = "DEALER_TURN";
        
        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
        
        resolveWinners();
    }

    private void resolveWinners() {
        this.gameStatus = "FINISHED";
        int dealerScore = dealerHand.calculateScore();
        boolean dealerBust = dealerHand.isBusted();
        boolean dealerBJ = dealerHand.isBlackjack();

        for (Seat seat : seats) {
            // Si le joueur a déjà perdu (Bust)
            if ("BUSTED".equals(seat.status)) {
                seat.status = "LOST";
                continue;
            }

            int playerScore = seat.hand.calculateScore();
            double multiplier = 0;
            boolean playerBJ = seat.hand.isBlackjack();

            if (playerBJ && dealerBJ) {
                seat.status = "PUSH"; // Egalité double Blackjack
                multiplier = 1.0;
            } else if (playerBJ) {
                seat.status = "WON BLACKJACK";
                multiplier = 2.5; // Blackjack naturel paie 3:2 (donc 2.5x mise initiale)
            } else if (dealerBJ) {
                seat.status = "LOST"; // Dealer a Blackjack, joueur non
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

            // Mise à jour de l'objet User en mémoire
            if (multiplier > 0) {
                BigDecimal gain = BigDecimal.valueOf(seat.bet * multiplier);
                seat.user.setBalance(seat.user.getBalance().add(gain));
            }
        }
        
        this.bettingEndTime = System.currentTimeMillis() + 10000; 
    }
    
    public void checkReset() {
        if ("FINISHED".equals(gameStatus) && System.currentTimeMillis() > bettingEndTime) {
            startBettingPhase();
        }
    }

    // --- API JSON ---
    public Map<String, Object> getGameState(AppUser requester) {
        checkTimer(); 
        checkReset(); 

        Map<String, Object> data = new HashMap<>();
        data.put("status", this.gameStatus);
        data.put("timer", getTimeRemaining());
        
        // Info Croupier
        Map<String, Object> dealerData = new HashMap<>();
        if ("BETTING".equals(gameStatus)) {
            dealerData.put("cards", Collections.emptyList());
            dealerData.put("score", 0);
        } else if ("PLAYER_TURN".equals(gameStatus)) {
            dealerData.put("cards", Collections.singletonList(dealerHand.getCards().get(0)));
            dealerData.put("score", "?");
        } else {
            dealerData.put("cards", dealerHand.getCards());
            dealerData.put("score", dealerHand.calculateScore());
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
            
            // Est-ce le tour de ce joueur ?
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