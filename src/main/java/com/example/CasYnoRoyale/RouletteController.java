package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.DTO.LockBetRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.CasYnoRoyale.service.AppUserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
    private HashMap<Long, Roulette> idRoulette = new HashMap<>();

    public RouletteController(AppUserService userService, AppUserRepository userRepository, RoomRepository roomRepository, RoomService roomService, GameRepository gameRepository, GameService gameService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    public Roulette getRoulette(Long id) {
        return idRoulette.get(id);
    }

    @GetMapping("/games/roulette")
    public String roulettePage(Model model,@RequestParam(required = false) Long idRoom,HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) {
            return "login";
        }
        if(idRoom == null){
            Room r=roomService.joinRoom(idRoom,user,gameService.getRoulette());
            idRoulette.put(r.getIdRoom(),new Roulette());
            return "redirect:/games/roulette?idRoom="+r.getIdRoom();
        }
        Room r = roomService.findRoomById(idRoom);
        model.addAttribute("user", user);
        model.addAttribute("room", r);
        return "roulette";

    }

    @PostMapping("/api/game/roulette/exit")
    public ResponseEntity<Void> exitRoulette(Model model, @RequestBody Long idRoom,HttpSession session){
        AppUser user = (AppUser) session.getAttribute("user");
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println(idRoom);
        roomService.exitRoom(idRoom,user);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/game/roulette/lockBet")
    public ResponseEntity<Map<String, Object>> lockBets(@RequestBody LockBetRequest requestBody, HttpSession session) {

        
        AppUser user = (AppUser) session.getAttribute("user");
        Long idRoom = new Long(requestBody.getIdRoom());
        for (BetRequest bet : requestBody.getBets()) {
            System.out.println("Type: " + bet.getBetType());
            System.out.println("Valeur: " + bet.getSelectionValue());
            System.out.println("Montant: " + bet.getAmount());
            getRoulette(idRoom).betDeposit(new Bet(user, bet.getAmount(), bet.getBetType(), bet.getSelectionValue(), bet.getSelectionValue()));
            System.out.println(user.getBalance().toString());
        }
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris acceptés");


        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/game/roulette/refreshData")
    public ResponseEntity<Map<String, Object>> refreshData(HttpSession session,@RequestBody Long idRoom) {
        AppUser user = (AppUser)session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris acceptés");
        response.put("nouveauSolde", user.getBalance());
        response.put("historique", getRoulette(idRoom).getTirages());
        response.put("prochainTirage", (ZonedDateTime)getRoulette(idRoom).getProchainTirage());
        return ResponseEntity.ok(response);}

    @PostMapping("/api/game/roulette/betcanceled")
    public ResponseEntity<Map<String, Object>> betCanceled(HttpSession session,@RequestBody Long idRoom) {
        AppUser user = (AppUser)session.getAttribute("user");
        getRoulette(idRoom).betCanceled(user);
        userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris annulés");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/game/roulette/tirer")
    public ResponseEntity<Map<String, Object>> tirer(HttpSession session,@RequestBody Long idRoom) {
        AppUser user = (AppUser)session.getAttribute("user");
        getRoulette(idRoom).tirer();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "tirer");

        return ResponseEntity.ok(response);
    }
}
