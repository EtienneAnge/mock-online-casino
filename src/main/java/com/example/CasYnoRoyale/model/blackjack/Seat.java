package com.example.CasYnoRoyale.model.blackjack;

import java.math.BigDecimal;

import com.example.CasYnoRoyale.database.AppUser;

public class Seat {
        public AppUser user;
        public Hand hand;
        public int bet;
        public String status;
        public BigDecimal initialBalance;

        public Seat(AppUser user, int bet) {
            this.user = user;
            this.bet = bet;
            this.hand = new Hand();
            this.status = "PLAYING";
            this.initialBalance = user.getBalance();
        }
    }