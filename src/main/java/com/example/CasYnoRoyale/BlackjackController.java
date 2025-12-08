package com.example.CasYnoRoyale;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.repository.RoomRepository;
import com.example.CasYnoRoyale.service.AppUserService;
import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomCodeService;
import com.example.CasYnoRoyale.service.RoomService;
import com.example.CasYnoRoyale.model.blackjack.Blackjack;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    // Récupérer l'instance de jeu associée à la salle
    public Blackjack getBlackjack(Long id) {
        return idTBlackjack.get(id);
    }

    @GetMapping("/games/blackjack")
    public String blackjackPage(Model model, String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) {
            return "login";
        }

        // Si pas d'ID de salle, on en crée une où on en rejoint une et on redirige
        if (idRoom == null) {
            // On assume que gameService.getBlackjack() existe (similaire à getRoulette)
            Room r = roomService.joinRoom(null, user, gameService.getBlackjack());
            // Initialisation d'une nouvelle partie de Blackjack pour cette salle
            idTBlackjack.put(r.getIdRoom(), new Blackjack());
            return "redirect:/games/blackjack?idRoom=" + roomCodeService.generateCode(r.getIdRoom());
        }

        Room r = roomService.findRoomById(roomCodeService.decodeRoomId(idRoom));
        model.addAttribute("user", user);
        model.addAttribute("room", r);
        return "blackjack"; // Le nom de votre vue HTML (Thymeleaf)
    }

    @PostMapping("/api/game/blackjack/exit")
    public ResponseEntity<Void> exitBlackjack(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("Sortie de la salle Blackjack : " + idRoom);
        roomService.exitRoom(idRoom, user);
        // On pourrait aussi nettoyer la map idTBlackjack si la salle est vide
        return ResponseEntity.ok().build();
    }

    // Remplacement de "lockBet" pour le Blackjack (Mise initiale)
    @PostMapping("/api/game/blackjack/bet")
    public ResponseEntity<Map<String, Object>> placeBet(@RequestBody Map<String, Object> payload, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        Long idRoom = Long.valueOf(payload.get("idRoom").toString());
        int amount = Integer.parseInt(payload.get("amount").toString());

        Blackjack game = getBlackjack(idRoom);
        
        // Logique de mise : on déduit du solde utilisateur et on l'ajoute au jeu
        // Note: Adaptez user.getBalance() selon si c'est un BigDecimal ou int/double
        if (user.getBalance().intValue() >= amount) {
            game.placeBet(user, amount); // Méthode supposée dans votre classe Blackjack
            // Mise à jour BDD (optionnel ici si géré par le service, mais fait dans RouletteController)
            userRepository.save(user); 
        } else {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Solde insuffisant"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Mise acceptée");
        response.put("nouveauSolde", user.getBalance());
        response.put("gameState", game.getGameState());
        
        return ResponseEntity.ok(response);
    }

    // Action "Hit" (Tirer une carte)
    @PostMapping("/api/game/blackjack/hit")
    public ResponseEntity<Map<String, Object>> playerHit(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        
        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.playerHit(); // Méthode supposée dans Blackjack

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carte tirée");
        response.put("gameState", game.getGameState()); // Récupérer les cartes/scores actuels

        return ResponseEntity.ok(response);
    }

    // Action "Stand" (Rester)
    @PostMapping("/api/game/blackjack/stand")
    public ResponseEntity<Map<String, Object>> playerStand(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.playerStand(); // Le croupier joue, puis fin de partie
        
        // Si le joueur a gagné, on met à jour le solde
        // Logique supposée : game.resolve(user) met à jour le solde de l'objet user
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tour terminé");
        response.put("nouveauSolde", user.getBalance());
        response.put("gameState", game.getGameState());

        return ResponseEntity.ok(response);
    }

    // Rafraichissement des données (similaire à Roulette)
    @PostMapping("/api/game/blackjack/refreshData")
    public ResponseEntity<Map<String, Object>> refreshData(HttpSession session, @RequestBody String idRoom) {
        AppUser user = (AppUser) session.getAttribute("user");
        
        Map<String, Object> response = new HashMap<>();
        response.put("nouveauSolde", user.getBalance());
        // getGameState() renverrait les cartes du joueur, du croupier et le statut (gagné/perdu)
        response.put("historique", getBlackjack(roomCodeService.decodeRoomId(idRoom)).getGameState());

        return ResponseEntity.ok(response);
    }
}