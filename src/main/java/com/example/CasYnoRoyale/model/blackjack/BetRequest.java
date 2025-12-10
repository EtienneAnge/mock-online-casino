package com.example.CasYnoRoyale.model.blackjack;

public class BetRequest {
    
    private String idRoom;
    private int amount;

    public BetRequest() {}

    public BetRequest(String idRoom, int amount) {
        this.idRoom = idRoom;
        this.amount = amount;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}