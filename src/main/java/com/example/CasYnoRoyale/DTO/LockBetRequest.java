package com.example.CasYnoRoyale.DTO;

import com.example.CasYnoRoyale.roulette.BetRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class LockBetRequest {

    @Getter
    @Setter
    private Long idRoom;
    @Getter
    @Setter
    private List<BetRequest> bets;
}
