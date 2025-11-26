package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomService;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.RoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class RouletteController {
    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final GameRepository gameRepository;
    private final GameService gameService;

    public RouletteController(RoomRepository roomRepository, RoomService roomService, GameRepository gameRepository, GameService gameService){
        this.roomRepository =  roomRepository;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }
    
    @GetMapping("/games/roulette")
    public String roulettePage(Model model,Long idRoom){
        if(idRoom == null){
            Room room = roomService.createNewRoom(gameService.getRoulette());
        }else{
           Room room = roomRepository.findById(idRoom)
    .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + idRoom));

        }
        // le user dois rejoindre la room !! (mathis)

        
        
        return "roulette";
    }

}
