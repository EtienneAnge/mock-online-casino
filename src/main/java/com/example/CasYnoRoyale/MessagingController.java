package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.database.Transaction;
import com.example.CasYnoRoyale.database.User;
import com.example.CasYnoRoyale.repository.UserRepository;
import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomService;
import com.example.CasYnoRoyale.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessagingController {
    private final UserService userService;
    private final RoomService roomService;
    private final GameService gameService;

    public MessagingController(UserService userService, RoomService roomService, GameService gameService) {
        this.userService = userService;
        this.roomService = roomService;
        this.gameService = gameService;
    }
    @MessageMapping("/room/join/roulette/{roomId}")
    public void handleJoinRoom(@DestinationVariable Long roomId, Principal principal ){
        String username = principal.getName();
        User user = userService.findByUserName(username);
        try{
            Room updatedRoom = roomService.joinRoom(roomId,user,gameService.getRoulette());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
