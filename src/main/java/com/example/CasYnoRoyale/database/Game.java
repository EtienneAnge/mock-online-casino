package com.example.CasYnoRoyale.database;


import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGame;

    private String label;
    private String url;

    @OneToMany(mappedBy = "game")
    private List<Room> rooms;
}