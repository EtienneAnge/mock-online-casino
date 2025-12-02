package com.example.CasYnoRoyale.roulette;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.CasYnoRoyale.database.User;

public class Bet {

    public enum BetType {
        // PARIS EXTERNES
        COLOR,         // 0=Rouge, 1=Noir
        PARITY,        // 0=Pair (Even), 1=Impair (Odd)
        LOW_HIGH,      // 0=Manque (1-18), 1=Passe (19-36)
        DOZEN,         // 0=1ère (1-12), 1=2ème (13-24), 2=3ème (25-36)
        COLUMN,        // 0=1ère col (1,4..), 1=2ème col (2,5..), 2=3ème col (3,6..)

        // PARIS INTERNES (On utilise la liste internValuesSelected)
        STRAIGHT_UP,   // Plein (x36)
        SPLIT,         // Cheval (x18)
        STREET,        // Transversale (x12)
        CORNER,        // Carré (x9)
        SIX_LINE       // Sixain (x6)
    }

    // Liste des numéros ROUGES à la roulette standard
    private static final List<Integer> RED_NUMBERS = Arrays.asList(
        1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    );

    User user;
    BigDecimal betValue;
    boolean canceled = false;
    
    // paris externes : 0 = rouge/pair/manque/1st; 1 = noir/impair/passe/2nd; 2 = 3rd
    int externValueSelected; 
    
    // paris internes : liste des numéros couverts par le jeton
    ArrayList<Integer> internValuesSelected; 
    
    BetType bt;

    public Bet(User user, BigDecimal betValue, BetType bt,int externValueSelected, ArrayList<Integer> internValuesSelected) {
        this.user = user;
        this.betValue = betValue;
        this.bt = bt;
        this.externValueSelected = externValueSelected;
        this.internValuesSelected = internValuesSelected;

        user.decreaseSolde(betValue);
    }

    public void cancelBet(){
        user.increaseSolde(betValue);
        canceled = true;
    }

    public boolean getCanceled(){
        return canceled;
    }

    public void fire(int numberDrawn) {
        // Si le zéro tombe, la plupart des paris externes perdent (sauf règle spéciale non implémentée ici)
        // Les switch case ci-dessous gèrent la logique standard.
        if(canceled) return;
        switch (bt) {
            // --- PARIS INTERNES (Inside Bets) ---
            // La logique est la même : si le numéro tiré est dans la liste, on gagne.
            // Seul le multiplicateur change.
            
            case STRAIGHT_UP: // Plein : 35 contre 1 (Total x36)
                if (internValuesSelected.contains(numberDrawn)) {
                    win(36);
                }
                break;

            case SPLIT: // Cheval : 17 contre 1 (Total x18)
                if (internValuesSelected.contains(numberDrawn)) {
                    win(18);
                }
                break;

            case STREET: // Transversale : 11 contre 1 (Total x12)
                if (internValuesSelected.contains(numberDrawn)) {
                    win(12);
                }
                break;

            case CORNER: // Carré : 8 contre 1 (Total x9)
                if (internValuesSelected.contains(numberDrawn)) {
                    win(9);
                }
                break;

            case SIX_LINE: // Sixain : 5 contre 1 (Total x6)
                if (internValuesSelected.contains(numberDrawn)) {
                    win(6);
                }
                break;

            // --- PARIS EXTERNES (Outside Bets) ---
            
            case COLOR: // Rouge (0) ou Noir (1) - x2
                if (numberDrawn == 0) break; // Le 0 n'est ni rouge ni noir (Perdu)
                boolean isRed = RED_NUMBERS.contains(numberDrawn);
                // Si (J'ai parié Rouge ET c'est Rouge) OU (J'ai parié Noir ET c'est pas Rouge)
                if ((externValueSelected == 0 && isRed) || (externValueSelected == 1 && !isRed)) {
                    win(2);
                }
                break;

            case PARITY: // Pair (0) ou Impair (1) - x2
                if (numberDrawn == 0) break; // Le 0 perd
                boolean isEven = (numberDrawn % 2 == 0);
                // Si (Pari Pair ET numéro Pair) OU (Pari Impair ET numéro Impair)
                if ((externValueSelected == 0 && isEven) || (externValueSelected == 1 && !isEven)) {
                    win(2);
                }
                break;

            case LOW_HIGH: // Manque 1-18 (0) ou Passe 19-36 (1) - x2
                if (numberDrawn == 0) break; // Le 0 perd
                boolean isLow = (numberDrawn >= 1 && numberDrawn <= 18);
                if ((externValueSelected == 0 && isLow) || (externValueSelected == 1 && !isLow)) {
                    win(2);
                }
                break;

            case DOZEN: // Douzaines - x3
                if (numberDrawn == 0) break; // Le 0 perd
                // 0 -> 1-12, 1 -> 13-24, 2 -> 25-36
                if (externValueSelected == 0 && (numberDrawn >= 1 && numberDrawn <= 12)) win(3);
                else if (externValueSelected == 1 && (numberDrawn >= 13 && numberDrawn <= 24)) win(3);
                else if (externValueSelected == 2 && (numberDrawn >= 25 && numberDrawn <= 36)) win(3);
                break;

            case COLUMN: // Colonnes - x3
                if (numberDrawn == 0) break; // Le 0 perd
                // Col 1 (1, 4, 7...) => Modulo 3 = 1
                // Col 2 (2, 5, 8...) => Modulo 3 = 2
                // Col 3 (3, 6, 9...) => Modulo 3 = 0
                int mod = numberDrawn % 3;
                if (externValueSelected == 0 && mod == 1) win(3);      // Colonne 1
                else if (externValueSelected == 1 && mod == 2) win(3); // Colonne 2
                else if (externValueSelected == 2 && mod == 0) win(3); // Colonne 3
                break;

            default:
                break;
        }
    }

    // Méthode helper pour éviter de répéter le code de gain
    private void win(int multiplier) {
        BigDecimal gain = betValue.multiply(BigDecimal.valueOf(multiplier));
        user.increaseSolde(gain);
   
    }
}