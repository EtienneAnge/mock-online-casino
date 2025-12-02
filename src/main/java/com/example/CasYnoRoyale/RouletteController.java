package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomService;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.database.User;
import com.example.CasYnoRoyale.repository.RoomRepository;
import com.example.CasYnoRoyale.roulette.Bet;
import com.example.CasYnoRoyale.roulette.BetRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
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

    @PostMapping("/api/game/routelle/lockBet")
    public ResponseEntity<String> lockBets(@RequestBody List<BetRequest> bets,User user,Room room) {
        
        if (bets == null || bets.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun pari reçu.");
        }

  
    

        for (BetRequest bet : bets) {
            System.out.println("Type: " + bet.getBetType());
            System.out.println("Valeur: " + bet.getSelectionValue());
            System.out.println("Montant: " + bet.getAmount());
            room.getRoulette().betDeposit(new Bet(user, bet.getAmount(), bet.getBetType(), bet.getSelectionValue(), bet.getSelectionValue()));
            
            
        }

        // 3. Réponse au client
        // On renvoie 200 OK avec un message ou le nouveau solde du joueur
        return ResponseEntity.ok("Paris enregistrés. Total misé : " + totalBetAmount);
    }
}
