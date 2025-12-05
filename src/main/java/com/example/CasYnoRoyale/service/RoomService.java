package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Game;


import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.RoomRepository;

import com.example.CasYnoRoyale.repository.AppUserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.NoSuchElementException;

@Service
public class RoomService {

    final RoomRepository roomRepository;
    final AppUserRepository userRepository;

    public RoomService(RoomRepository roomRepository, AppUserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository= userRepository;
    }

    public Room createNewRoom(Game g){
        Room r = new Room();
        r.setGame(g);
        r.setUsers(new LinkedList<>());
        roomRepository.save(r);
        return r;
    }

    public Room findRoomById(Long roomId){
        return roomRepository.getReferenceById(roomId);
    }

    @Transactional
    public Room joinRoom(Long roomId, AppUser detachedUser, Game game){
        Room r;
        AppUser player = userRepository.findById(detachedUser.getIdUser()).orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé en base de données"));
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

    public void exitRoom(@RequestParam Long roomId, AppUser player){
        if(roomId != null){
            Room r = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + roomId));
            r.getUsers().remove(player);
            roomRepository.save(r);
        }
    }
}