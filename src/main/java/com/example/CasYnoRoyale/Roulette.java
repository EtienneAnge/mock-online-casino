package com.example.CasYnoRoyale;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.example.CasYnoRoyale.database.AppUser;

public class Roulette {
    ArrayList<AppUser> players  = new ArrayList<AppUser>();
    ArrayList<Integer> tirages = new ArrayList<>();

     
    ZonedDateTime prochainTirage;

    public Roulette(){
        resetTime();
    }

    public int getLastTirage(){
        return tirages.get(tirages.size() - 1);

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
