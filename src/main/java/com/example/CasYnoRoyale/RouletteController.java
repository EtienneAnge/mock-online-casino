package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoleService;
import com.example.CasYnoRoyale.service.RoomService;

import com.example.CasYnoRoyale.database.Game;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.repository.RoomRepository;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.roulette.Bet;
import com.example.CasYnoRoyale.roulette.Roulette;
import jakarta.servlet.http.HttpSession;

import com.example.CasYnoRoyale.roulette.BetRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.CasYnoRoyale.service.AppUserService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
public class RouletteController {
    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final AppUserRepository userRepository;
    private final AppUserService userService;
    private HashMap<Long,Roulette> idTRoulette = new HashMap();

    public RouletteController(AppUserService userService, AppUserRepository userRepository,RoomRepository roomRepository, RoomService roomService, GameRepository gameRepository, GameService gameService){
        this.userService= userService;
        this.userRepository = userRepository;
        this.roomRepository =  roomRepository;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        idTRoulette.put(new Long(0),new Roulette());
    }

    public Roulette getRoulette(Long id){
        return idTRoulette.get(id);
    }
    
    @GetMapping("/games/roulette")
    public String roulettePage(Model model, HttpSession session){
        
    //     if(idRoom == null){
    //         Room room = roomService.createNewRoom(gameService.getRoulette());
    //     }else{
    //        Room room = roomRepository.findById(idRoom)
    // .orElseThrow(() -> new NoSuchElementException("Salle non trouvée avec l'ID : " + idRoom));

    //     }
        // le user dois rejoindre la room !! (mathis)
        AppUser user = (AppUser)session.getAttribute("user");
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
         System.out.println(user.getBalance().toString());;

        
        return "roulette";
    }

    @PostMapping("/api/game/routelle/lockBet")
    public ResponseEntity<Map<String, Object>> lockBets(@RequestBody List<BetRequest> bets, HttpSession session) {
        
      
  
        AppUser user = (AppUser)session.getAttribute("user");

        for (BetRequest bet : bets) {
            System.out.println("Type: " + bet.getBetType());
            System.out.println("Valeur: " + bet.getSelectionValue());
            System.out.println("Montant: " + bet.getAmount());
            getRoulette(new Long(0)).betDeposit(new Bet(user, bet.getAmount(), bet.getBetType(), bet.getSelectionValue(), bet.getSelectionValue()));
            System.out.println(user.getBalance().toString());;
            
        }
                userRepository.save(user);


        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris acceptés");
        response.put("nouveauSolde", user.getBalance());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/game/routelle/betcanceled")
    public String betCanceled(HttpSession session){
        AppUser user = (AppUser)session.getAttribute("user");

        getRoulette(new Long(0)).betCanceled(user);
        userRepository.save(user);
        return null;
    }
}
