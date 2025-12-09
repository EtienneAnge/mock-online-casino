package com.example.CasYnoRoyale.roulette;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.CasYnoRoyale.database.AppUser;
public class BetRequest {
    private Bet.BetType betType;       
    private Integer selectionValue; 
    private BigDecimal amount;      

    public Bet.BetType getBetType() { return betType; }
    public void setBetType(Bet.BetType betType) { this.betType = betType; }

    public Integer getSelectionValue() { return selectionValue; }
    public void setSelectionValue(Integer selectionValue) { this.selectionValue = selectionValue; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}