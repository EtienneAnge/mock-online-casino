package com.example.CasYnoRoyale.roulette;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.example.CasYnoRoyale.database.User;

public class Roulette {
    ArrayList<Bet> bets  = new ArrayList<Bet>();
    ArrayList<Integer> tirages = new ArrayList<>();

     
    ZonedDateTime prochainTirage;

    public Roulette(){
        resetTime();
    }

    public int getLastTirage(){
        return tirages.get(tirages.size()-1);

    }

    public void betDeposit(Bet b){
        bets.add(b);
        
    }

    public int tirer(){
        if(ZonedDateTime.now().isAfter(prochainTirage)){
            int tirage = (int) (Math.random() * 37);
            tirages.add(tirage);
            resetTime();

            for (Bet bet : bets) {
                bet.fire(tirage);
                
            }
            bets.clear();

            return tirage;

        }
        return -1;
        
    }

    private void resetTime(){
        ZonedDateTime ancienTirage = ZonedDateTime.now();
        prochainTirage = ancienTirage.plusMinutes(1);
    }


}
