package com.example.CasYnoRoyale;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.example.CasYnoRoyale.database.User;

public class Roulette {
    ArrayList<User> players  = new ArrayList<User>();
    ArrayList<Integer> tirages = new ArrayList<>();

     
    ZonedDateTime prochainTirage;

    public Roulette(){
        resetTime();
    }

    public int getLastTirage(){
        return tirages.getLast();

    }

    
    public int tirer(){
        if(ZonedDateTime.now().isAfter(prochainTirage)){
            int tirage = (int) (Math.random() * 37);
            tirages.add(tirage);
            resetTime();
            return tirage;
        }
        return -1;
        
    }

    private void resetTime(){
        ZonedDateTime ancienTirage = ZonedDateTime.now();
        prochainTirage = ancienTirage.plusMinutes(1);
    }


}
