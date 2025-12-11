package com.example.CasYnoRoyale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.example.CasYnoRoyale.database.Transaction;
import com.example.CasYnoRoyale.model.blackjack.BetRequest;
import com.example.CasYnoRoyale.model.blackjack.Blackjack;
import com.example.CasYnoRoyale.model.blackjack.Seat;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.repository.TransactionRepository;
import com.example.CasYnoRoyale.service.GameService;
import com.example.CasYnoRoyale.service.RoomCodeService;
import com.example.CasYnoRoyale.service.RoomService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BlackjackController {

    private final AppUserRepository userRepository;
    private final RoomService roomService;
    private final GameService gameService;
    private final RoomCodeService roomCodeService;
    private final TransactionRepository transactionRepository;
    
    private final HashMap<Long, Blackjack> idTBlackjack = new HashMap<>();

    public BlackjackController(AppUserRepository userRepository, RoomService roomService, GameService gameService, RoomCodeService roomCodeService, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.roomService = roomService;
        this.gameService = gameService;
        this.roomCodeService = roomCodeService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Récupère ou crée une instance de jeu Blackjack associée à une salle.
     * Utilise {@code computeIfAbsent} pour garantir qu'une seule instance existe par salle.
     *
     * @param idRoom L'identifiant unique de la salle.
     * @return L'instance de Blackjack correspondante.
     */
    public Blackjack getBlackjack(Long idRoom) {
        if (idRoom == null) return new Blackjack();
        return idTBlackjack.computeIfAbsent(idRoom, k -> new Blackjack());
    }

    /**
     * Sauvegarde l'état des utilisateurs (solde) en base de données à la fin d'une manche
     * et crée une transaction pour l'historique des gains/pertes.
     *
     * @param game L'instance de jeu terminée.
     * @param idRoom @param game L'instance de jeu terminée.
     */
    private void saveGameResults(Blackjack game, String idRoom) {
        if (game.isResultsSaved()) return;

        Room room = roomService.findRoomById(roomCodeService.decodeRoomId(idRoom));
        for (Seat seat : game.getSeats()) {
            userRepository.save(seat.user);

            Transaction trans = new Transaction();
            trans.setGame(gameService.getBlackjack());
            trans.setUser(seat.user);
            trans.setDate(LocalDateTime.now());

            BigDecimal currentBalance = seat.user.getBalance();
            BigDecimal startBalance = seat.initialBalance;
            BigDecimal betAmount = BigDecimal.valueOf(seat.bet);

            BigDecimal payout = currentBalance.subtract(startBalance);

            BigDecimal netProfit = payout.subtract(betAmount);

            trans.setMontant(netProfit);
            transactionRepository.save(trans);
        }
        game.setResultsSaved(true);
    }

    /**
     * Point d'entrée principal pour accéder à la page du Blackjack.
     * 
     * @param model   Le modèle Spring pour passer des données à la vue (Thymeleaf).
     * @param idRoom  Le code crypté de la salle.
     * @param session La session HTTP courante.
     * @return Le nom du template HTML ("blackjack") ou une redirection.
     */
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

    /**
     * API : Permet à un utilisateur de quitter la salle.
     *
     * @param idRoom  Le code crypté de la salle.
     * @param session La session HTTP courante.
     * @return Réponse 200 OK si succès.
     */
    @PostMapping("/api/game/blackjack/exit")
    public ResponseEntity<Void> exitBlackjack(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        roomService.exitRoom(idRoom, user);
        return ResponseEntity.ok().build();
    }

    /**
     * API : Gère le placement d'une mise.
     * Vérifie le solde, débite le compte utilisateur et enregistre la mise dans le moteur de jeu.
     *
     * @param request Objet DTO contenant l'ID de la salle (code) et le montant.
     * @param session La session HTTP courante.
     * @return L'état du jeu (JSON) ou une erreur 400 si solde insuffisant/données invalides.
     */
    @PostMapping("/api/game/blackjack/bet")
    public ResponseEntity<Map<String, Object>> placeBet(@RequestBody BetRequest request, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (request.getIdRoom() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Données de mise invalides"));
        }

        Long safeId = roomCodeService.decodeRoomId(request.getIdRoom());
        Blackjack game = getBlackjack(safeId);
        BigDecimal betAmount = BigDecimal.valueOf(request.getAmount());

        if (user.getBalance().compareTo(betAmount) >= 0) {
            user.setBalance(user.getBalance().subtract(betAmount));
            userRepository.save(user); 
            game.placeBet(user, request.getAmount());
            session.setAttribute("user", user);
            return ResponseEntity.ok(game.getGameState(user));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Solde insuffisant"));
        }
    }

    /**
     * API : Action "Tirer une carte" (Hit).
     *
     * @param idRoom  Le code crypté de la salle.
     * @param session La session HTTP courante.
     * @return L'état du jeu mis à jour.
     */
    @PostMapping("/api/game/blackjack/hit")
    public ResponseEntity<Map<String, Object>> playerHit(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.hit(user); 
        
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game, idRoom);
        }

        return ResponseEntity.ok(game.getGameState(user));
    }

    /**
     * API : Action "Rester" (Stand).
     *
     * @param idRoom  Le code crypté de la salle.
     * @param session La session HTTP courante.
     * @return L'état du jeu mis à jour.
     */
    @PostMapping("/api/game/blackjack/stand")
    public ResponseEntity<Map<String, Object>> playerStand(@RequestBody String idRoom, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        game.stand(user);
        
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game, idRoom);
        }

        return ResponseEntity.ok(game.getGameState(user));
    }

    /**
     * API : Rafraîchissement des données (Polling).
     * Appelé périodiquement par le client pour synchroniser l'état du jeu.
     *
     * @param session La session HTTP courante.
     * @param idRoom  Le code crypté de la salle.
     * @return L'état complet du jeu ainsi que le solde à jour de l'utilisateur.
     */
    @PostMapping("/api/game/blackjack/refreshData")
    public ResponseEntity<Map<String, Object>> refreshData(HttpSession session, @RequestBody String idRoom) {
        AppUser user = (AppUser) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Blackjack game = getBlackjack(roomCodeService.decodeRoomId(idRoom));
        
        if ("FINISHED".equals(game.getStatus())) {
            saveGameResults(game, idRoom);
        }

        Map<String, Object> state = game.getGameState(user);
        
        AppUser freshUser = userRepository.findByUsername(user.getUsername());
        state.put("userBalance", freshUser.getBalance());
        
        if(!freshUser.getBalance().equals(user.getBalance())) {
            session.setAttribute("user", freshUser);
        }

        return ResponseEntity.ok(state);
    }
}