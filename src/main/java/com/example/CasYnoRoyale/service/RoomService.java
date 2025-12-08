package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Game;


import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.RoomRepository;

import com.example.CasYnoRoyale.repository.AppUserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.NoSuchElementException;

@Service
public class RoomService {

    final RoomRepository roomRepository;
    final AppUserRepository userRepository;
    final RoomCodeService roomCodeService;
    public RoomService(RoomRepository roomRepository, AppUserRepository userRepository,RoomCodeService roomCodeService) {
        this.roomRepository = roomRepository;
        this.userRepository= userRepository;
        this.roomCodeService = roomCodeService;
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
    public Room joinRoom(String roomId, AppUser detachedUser, Game game){
        Room r;
        AppUser player = userRepository.findByUsername(detachedUser.getUsername());
        if(player==null){
            throw new NoSuchElementException();
        }
        if(roomId == null){
            if(game==null){
                return null;
            }
            r=createNewRoom(game);

        }else {
            r = roomRepository.findById(roomCodeService.decodeRoomId(roomId))
                    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + roomId));
        }
        r.getUsers().add(player);
        player.getRooms().add(r);
        roomRepository.save(r);
        userRepository.save(player);
        return r;
    }

    public void exitRoom(String roomId, AppUser detachedUser){
        Long code = roomCodeService.decodeRoomId(roomId);
        if(roomId != null){
            AppUser player = userRepository.findByUsername(detachedUser.getUsername());
            if(player==null){
                throw new NoSuchElementException();
            }
            Room r = roomRepository.findById(roomCodeService.decodeRoomId(roomId))
                    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + roomId));
            System.out.println(r.getUsers());
            player.getRooms().remove(r);
            r.getUsers().remove(player);
            if(r.getUsers().isEmpty()){
                roomRepository.delete(r);
            }
            userRepository.save(player);
        }
    }
}