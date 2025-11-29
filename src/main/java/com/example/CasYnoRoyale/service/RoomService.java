package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.database.User;


import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.RoomRepository;

import com.example.CasYnoRoyale.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RoomService {

    final RoomRepository roomRepository;
    final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository= userRepository;
    }

    public Room createNewRoom(Game g){
        Room r = new Room();
        r.setGame(g);
        roomRepository.save(r);
        return r;
    }

    public Room findRoomById(Long roomId){
        return roomRepository.getReferenceById(roomId);
    }

    public Room joinRoom(Long roomId, User player,Game game){
        Room r;
        if(roomId == null){
            r=createNewRoom(game);
        }else {
            r = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + roomId));
        }
        r.getUsers().add(player);
        player.getRooms().add(r);
        roomRepository.save(r);
        userRepository.save(player);
        return r;
    }

    public boolean exitRoom(Long roomId, User player){
        if(roomId == null){
            return false;
        }else{
            Room r = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + roomId));
            r.getUsers().remove(player);
            roomRepository.save(r);
            return true;
        }


    }
}