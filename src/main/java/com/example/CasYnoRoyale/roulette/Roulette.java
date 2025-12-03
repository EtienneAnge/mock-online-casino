package com.example.CasYnoRoyale.roulette;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.CasYnoRoyale.database.AppUser;

public class Roulette {
<<<<<<< HEAD:src/main/java/com/example/CasYnoRoyale/Roulette.java
    ArrayList<AppUser> players  = new ArrayList<AppUser>();
=======
    ArrayList<Bet> bets  = new ArrayList<Bet>();
>>>>>>> main:src/main/java/com/example/CasYnoRoyale/roulette/Roulette.java
    ArrayList<Integer> tirages = new ArrayList<>();

     
    ZonedDateTime prochainTirage;

    public Roulette(){
        resetTime();
    }

    public int getLastTirage(){
<<<<<<< HEAD:src/main/java/com/example/CasYnoRoyale/Roulette.java
        return tirages.get(tirages.size() - 1);
=======
        return tirages.get(tirages.size()-1);
>>>>>>> main:src/main/java/com/example/CasYnoRoyale/roulette/Roulette.java

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

    public void betCanceled(User user) {
    Iterator<Bet> iterator = bets.iterator();
    
    while (iterator.hasNext()) {
        Bet bet = iterator.next();
        
        if (bet.getUser().getIdUser().equals(user.getIdUser())) {
            
            bet.cancelBet(); 
            
            iterator.remove(); 
        }
    }
}


}
