package com.example.CasYnoRoyale;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.model.blackjack.Blackjack;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.repository.RoomRepository;
import com.example.CasYnoRoyale.service.AppUserService;
import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomCodeService;
import com.example.CasYnoRoyale.service.RoomService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BlackjackController {

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final AppUserRepository userRepository;
    private final AppUserService userService;
    private final RoomCodeService roomCodeService;
    // Stockage de l'instance de jeu par ID de salle, comme dans RouletteController
    private HashMap<Long, Blackjack> idTBlackjack = new HashMap<>();

    public BlackjackController(AppUserService userService, AppUserRepository userRepository, RoomRepository roomRepository, RoomService roomService, RoomCodeService roomCodeService, GameRepository gameRepository, GameService gameService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.roomCodeService = roomCodeService;
    }

    public Blackjack getBlackjack(Long idRoom) {
        if (!idTBlackjack.containsKey(idRoom)) {
            idTBlackjack.put(idRoom, new Blackjack());
        }
        return idTBlackjack.get(idRoom);
    }

    // --- CORRECTION 1 : Méthode pour sauvegarder les gains ---
    private void saveGameResults(Blackjack game) {
        // On parcourt tous les sièges et on sauvegarde les utilisateurs en BDD
        // car leurs soldes ont été modifiés dans le modèle (Blackjack.java)
        for (Blackjack.Seat seat : game.getSeats()) {
            userRepository.save(seat.user);
        }
    }

    @GetMapping("/games/blackjack")
    public String blackjackPage(Model model, String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (idRoom == null) {
            Room r = roomService.joinRoom(null, user, gameService.getBlackjack());
            if (!idTBlackjack.containsKey(r.getIdRoom())) {
                idTBlackjack.put(r.getIdRoom(), new Blackjack());
            }
            return "redirect:/games/blackjack?idRoom=" + roomCodeService.generateCode(r.getIdRoom());
        }

        Room r = roomService.findRoomById(roomCodeService.decodeRoomId(idRoom));
        model.addAttribute("user", user);
        model.addAttribute("room", r);
        
        return "blackjack"; 
    }

    @PostMapping("/api/game/blackjack/exit")
    public ResponseEntity<Void> exitBlackjack(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        roomService.exitRoom(idRoom, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/game/blackjack/bet")
    public ResponseEntity<Map<String, Object>> placeBet(@RequestBody Map<String, Object> payload, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            //Long idRoom = Long.valueOf(payload.get("idRoom").toString());
            int amount = Integer.parseInt(payload.get("amount").toString());
            BigDecimal betAmount = BigDecimal.valueOf(amount);

            Blackjack game = getBlackjack(roomCodeService.decodeRoomId(/*idRoom*/payload.get("idRoom").toString()));
            
            if (user.getBalance().compareTo(betAmount) >= 0) {
                user.setBalance(user.getBalance().subtract(betAmount));
                userRepository.save(user); 
                
                game.placeBet(user, amount);
                session.setAttribute("user", user);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Solde insuffisant"));
            }

            return ResponseEntity.ok(game.getGameState(user));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/game/blackjack/hit")
    public ResponseEntity<Map<String, Object>> playerHit(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.hit(user); 
        
        // Si le hit provoque la fin de la partie (ex: tout le monde a bust ou fini), on sauvegarde
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game);
        }

        return ResponseEntity.ok(game.getGameState(user));
    }

    @PostMapping("/api/game/blackjack/stand")
    public ResponseEntity<Map<String, Object>> playerStand(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.stand(user);
        
        // CORRECTION 1 : Si le jeu est FINI, on sauvegarde tout le monde
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game);
        }

        return ResponseEntity.ok(game.getGameState(user));
    }

    @PostMapping("/api/game/blackjack/refreshData")
    public ResponseEntity<Map<String, Object>> refreshData(HttpSession session, @RequestBody String idRoom) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        
        // CORRECTION 1 : Sécurité
        // Si le timer a déclenché la fin de la partie sans qu'une action "hit/stand" ne soit appelée
        // (ex: dernier joueur timeout ou logique auto), on sauvegarde ici aussi.
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game);
        }

        Map<String, Object> state = game.getGameState(user);
        
        // On recharge depuis la BDD pour avoir le solde à jour (qui vient d'être sauvegardé juste au-dessus)
        AppUser freshUser = userRepository.findByUsername(user.getUsername());//.orElse(user);
        state.put("userBalance", freshUser.getBalance());
        
        if(!freshUser.getBalance().equals(user.getBalance())) {
            session.setAttribute("user", freshUser);
        }

        return ResponseEntity.ok(state);
    }
}