package com.example.CasYnoRoyale.database;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.example.CasYnoRoyale.roulette.Roulette;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoom;

    private LocalDateTime date; 

    @ManyToOne
    @JoinColumn(name = "idGame", nullable = false)
    private Game game;

    
    @ManyToMany(mappedBy = "rooms")
    private List<User> users;

    @OneToMany(mappedBy = "room")
    private List<Transaction> transactions;

    private Roulette rouletteInstance;

    public Roulette getRoulette(){
        return rouletteInstance;
    }
}