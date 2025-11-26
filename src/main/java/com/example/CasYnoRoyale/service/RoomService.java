package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;


import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.RoomRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class RoomService {

    final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    public Room createNewRoom(Game g){
        Room r = new Room();
        r.setGame(g);
        roomRepository.save(r);
        return r;
    }
}