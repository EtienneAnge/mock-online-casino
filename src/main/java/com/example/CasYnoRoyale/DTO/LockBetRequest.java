package com.example.CasYnoRoyale.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.CasYnoRoyale.model.roulette.BetRequest;

public class LockBetRequest {
    @Getter
    @Setter
    private String idRoom;
    @Getter
    @Setter
    private List<BetRequest> bets;
}
