package com.example.CasYnoRoyale.model.roulette;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.database.Transaction;
import com.example.CasYnoRoyale.repository.TransactionRepository;

public class Bet {

    public enum BetType {

        COLOR,         
        PARITY,        
        LOW_HIGH,      
        DOZEN,         
        COLUMN,        

        STRAIGHT_UP,   
        SPLIT,         
        STREET,        
        CORNER,        
        SIX_LINE       
    }

    private static final List<Integer> RED_NUMBERS = Arrays.asList(
        1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    );

    AppUser user;
    BigDecimal betValue;
    boolean canceled = false;
    boolean win = false;
    int externValueSelected; 
    Room room;
TransactionRepository transactionRepository;
    ArrayList<Integer> internValuesSelected; 

    BetType bt;

    public Bet(AppUser user, Room room, BigDecimal betValue, BetType bt,Integer externValueSelected, Integer internValuesSelected, TransactionRepository transactionRepository) {
        this.user = user;
        this.room = room;
        this.betValue = betValue;
        this.bt = bt;
        this.externValueSelected = externValueSelected;
        this.internValuesSelected = new ArrayList<Integer>(internValuesSelected);
        this.transactionRepository =  transactionRepository;

        try {
            user.decreaseBalance(betValue);
        } catch (Exception e) {
            canceled = true;
            throw e;
        }

    }

    public AppUser getUser(){
        return user;
    }

    public void cancelBet(){
        user.increaseBalance(betValue);
        canceled = true;
    }

    public boolean getCanceled(){
        return canceled;
    }

    public void fire(int numberDrawn) {

        if(canceled) return;
        switch (bt) {

            case STRAIGHT_UP: 
                if (internValuesSelected.contains(numberDrawn)) {
                    win(36);
                }
                break;

            case SPLIT: 
                if (internValuesSelected.contains(numberDrawn)) {
                    win(18);
                }
                break;

            case STREET: 
                if (internValuesSelected.contains(numberDrawn)) {
                    win(12);
                }
                break;

            case CORNER: 
                if (internValuesSelected.contains(numberDrawn)) {
                    win(9);
                }
                break;

            case SIX_LINE: 
                if (internValuesSelected.contains(numberDrawn)) {
                    win(6);
                }
                break;

            case COLOR: 
                if (numberDrawn == 0) break; 
                boolean isRed = RED_NUMBERS.contains(numberDrawn);

                if ((externValueSelected == 0 && isRed) || (externValueSelected == 1 && !isRed)) {
                    win(2);
                }
                break;

            case PARITY: 
                if (numberDrawn == 0) break; 
                boolean isEven = (numberDrawn % 2 == 0);

                if ((externValueSelected == 0 && isEven) || (externValueSelected == 1 && !isEven)) {
                    win(2);
                }
                break;

            case LOW_HIGH: 
                if (numberDrawn == 0) break; 
                boolean isLow = (numberDrawn >= 1 && numberDrawn <= 18);
                if ((externValueSelected == 0 && isLow) || (externValueSelected == 1 && !isLow)) {
                    win(2);
                }
                break;

            case DOZEN: 
                if (numberDrawn == 0) break; 

                if (externValueSelected == 0 && (numberDrawn >= 1 && numberDrawn <= 12)) win(3);
                else if (externValueSelected == 1 && (numberDrawn >= 13 && numberDrawn <= 24)) win(3);
                else if (externValueSelected == 2 && (numberDrawn >= 25 && numberDrawn <= 36)) win(3);
                break;

            case COLUMN: 
                if (numberDrawn == 0) break; 

                int mod = numberDrawn % 3;
                if (externValueSelected == 0 && mod == 1) win(3);      
                else if (externValueSelected == 1 && mod == 2) win(3); 
                else if (externValueSelected == 2 && mod == 0) win(3); 
                break;

            default:
                break;
        }
        if(!win){
            Transaction trans = new Transaction();
            trans.setDate(LocalDateTime.now());
            trans.setGame(room.getGame());
            trans.setUser(user);
            trans.setMontant(betValue.negate());
            transactionRepository.save(trans);

        }
    }

    private void win(int multiplier) {
        BigDecimal gain = betValue.multiply(BigDecimal.valueOf(multiplier));
        user.increaseBalance(gain);
        win = true;
        Transaction trans = new Transaction();
            trans.setDate(LocalDateTime.now());
            trans.setGame(room.getGame());
            trans.setUser(user);
            trans.setMontant(gain.subtract(betValue));
            transactionRepository.save(trans);
    }
}