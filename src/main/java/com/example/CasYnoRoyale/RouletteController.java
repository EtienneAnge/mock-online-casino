package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.DTO.LockBetRequest;
import com.example.CasYnoRoyale.service.*;

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
    private final RoomCodeService roomCodeService;
    private HashMap<Long, Roulette> idRoulette = new HashMap<>();

    public RouletteController(AppUserService userService, AppUserRepository userRepository, RoomRepository roomRepository, RoomCodeService roomCodeService, RoomService roomService, GameRepository gameRepository, GameService gameService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.roomCodeService=roomCodeService;
    }

    public Roulette getRoulette(Long id) {
        return idRoulette.get(id);
    }

    @GetMapping("/games/roulette")
    public String roulettePage(Model model,@RequestParam(required = false) String idRoom,HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) {
            return "login";
        }
        if(idRoom == null){
            Room r=roomService.joinRoom(null,user,gameService.getRoulette());
            idRoulette.put(r.getIdRoom(),new Roulette());
            return "redirect:/games/roulette?idRoom="+roomCodeService.generateCode(r.getIdRoom());
        }
        Room r = roomService.findRoomById(roomCodeService.decodeRoomId(idRoom));
        model.addAttribute("user", user);
        model.addAttribute("room", r);
        return "roulette";

    }

    @PostMapping("/api/game/roulette/exit")
    public ResponseEntity<Void> exitRoulette(Model model, @RequestBody String idRoom,HttpSession session){
        AppUser user = (AppUser) session.getAttribute("user");
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        roomService.exitRoom(idRoom,user);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/game/roulette/lockBet")
    public ResponseEntity<Map<String, Object>> lockBets(@RequestBody LockBetRequest requestBody, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        String idRoom = requestBody.getIdRoom();
        for (BetRequest bet : requestBody.getBets()) {
            System.out.println("Type: " + bet.getBetType());
            System.out.println("Valeur: " + bet.getSelectionValue());
            System.out.println("Montant: " + bet.getAmount());
            getRoulette(roomCodeService.decodeRoomId(idRoom)).betDeposit(new Bet(user, bet.getAmount(), bet.getBetType(), bet.getSelectionValue(), bet.getSelectionValue()));
            System.out.println(user.getBalance().toString());
        }
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris acceptés");


        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/game/roulette/refreshData")
    public ResponseEntity<Map<String, Object>> refreshData(HttpSession session,@RequestBody String idRoom) {
        AppUser user = (AppUser)session.getAttribute("user");
        System.out.println(roomCodeService.decodeRoomId(idRoom));
        int t = getRoulette(roomCodeService.decodeRoomId(idRoom)).tirer();

        Map<String, Object> response = new HashMap<>();
        if(t != -1){
            response.put("tirage", t);
        }
        response.put("message", "Paris acceptés");
        response.put("nouveauSolde", user.getBalance());
        response.put("historique", getRoulette(roomCodeService.decodeRoomId(idRoom)).getTirages());
        response.put("prochainTirage", (ZonedDateTime)getRoulette(roomCodeService.decodeRoomId(idRoom)).getProchainTirage());
        return ResponseEntity.ok(response);}

    @PostMapping("/api/game/roulette/betcanceled")
    public ResponseEntity<Map<String, Object>> betCanceled(HttpSession session,@RequestBody String idRoom) {
        AppUser user = (AppUser)session.getAttribute("user");
        getRoulette(roomCodeService.decodeRoomId(idRoom)).betCanceled(user);
        userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paris annulés");

        return ResponseEntity.ok(response);
    }

    
}
