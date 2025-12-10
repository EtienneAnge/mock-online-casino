package com.example.CasYnoRoyale.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.database.Room;
import com.example.CasYnoRoyale.database.Transaction;
import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.repository.RoomRepository;
import com.example.CasYnoRoyale.repository.TransactionRepository;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.repository.GameRepository;
import com.example.CasYnoRoyale.database.AppUser; // Assurez-vous que les imports sont corrects
import com.example.CasYnoRoyale.database.Game;

@Configuration
public class DataInitializer {

    private Role createRoleIfNotFound(RoleRepository repo, String label, int level) {
        Role role = repo.findByLabel(label);
        if (role == null) {
            role = new Role();
            role.setLabel(label);
            role.setLevel(level);
            return repo.save(role);
        }
        return role;
    }

//    // C'EST CETTE MÉTHODE QUI TE MANQUAIT :
    private Room createRoomIfNotFound(RoomRepository repo, Game game) {
        // Logique simplifiée : on crée une salle pour le jeu si on veut des données de
        // test
        // Idéalement on vérifierait si une salle existe déjà pour ce jeu
        Room r = new Room();
        r.setGame(game);
        r.setDate(LocalDateTime.now());
        return repo.save(r);
    }

    // Cette méthode s'exécute juste après le démarrage du contexte Spring
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository,
                                        AppUserRepository userRepository,
                                        TransactionRepository transactionRepository,
                                        GameRepository gameRepository,
                                        RoomRepository roomRepository) {

        // Le corps de la fonction à exécuter au démarrage
        return args -> {

            // Initialisation des rôles

            // Role User
            if (roleRepository.findByLabel("ROLE_USER") == null) {
                Role userRole = new Role();
                userRole.setLabel("ROLE_USER");
                userRole.setLevel(0);
                roleRepository.save(userRole);
                System.out.println("Rôle 'ROLE_USER' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_USER' déjà existant.");
            }

            // Role Admin
            if (roleRepository.findByLabel("ROLE_ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setLabel("ROLE_ADMIN");
                adminRole.setLevel(1);
                roleRepository.save(adminRole);
                System.out.println("Rôle 'ROLE_ADMIN' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_ADMIN' déjà existant.");
            }

            // Initialisation compte admin
            // Role Admin
            AppUser adminUser = userRepository.findByUsername("admin");
            if (adminUser == null) {
                adminUser = new AppUser();

                Role adminRoleForUser = roleRepository.findByLabel("ROLE_ADMIN");

                if (adminRoleForUser == null) {
                    System.out.println("ERREUR CRITIQUE: Le rôle 'ROLE_USER' n'est pas initialisé en base de données.");
                }

                adminUser.setBalance(BigDecimal.ZERO);
                adminUser.setRole(adminRoleForUser);
                adminUser.setName("admin");
                adminUser.setUsername("admin");
                adminUser.setPassword("$2a$10$gaErcQZtGyJBZloJkL0Ehu5Y/7pWdLFajNsn2e4m7WTzcGIBTuqem");

                userRepository.save(adminUser);
              
                System.out.println("User 'admin' créé et inséré en BDD.");
            } else {
                System.out.println("User 'admin' déjà existant.");
            }

            Game blackjack = gameRepository.findByLabel("Blackjack");

            Game roulette = gameRepository.findByLabel("Roulette");
            if (blackjack == null) {
                blackjack = new Game();
                blackjack.setLabel("Blackjack");
                blackjack.setUrl("/games/blackjack");
                gameRepository.save(blackjack);

            }
            if (roulette == null) {
                roulette = new Game();
                roulette.setLabel("Roulette");
                roulette.setUrl("/games/roulette");
                gameRepository.save(roulette);

            }
            // --- 3. INITIALISATION DES SALLES (ROOMS) ---
            // On crée une salle par défaut pour chaque jeu
            Room roomBlackjack = createRoomIfNotFound(roomRepository, blackjack);
            Room roomRoulette = createRoomIfNotFound(roomRepository, roulette);
            // --- 5. CRÉATION DES TRANSACTIONS (Si pas encore présentes) ---
            // On vérifie s'il y a déjà des transactions pour ne pas les dupliquer à chaque restart
            if (transactionRepository.count() == 0) {
                System.out.println("Génération de transactions de test...");
                Random random = new Random();
                BigDecimal currentBalance = adminUser.getBalance();

                // Boucle pour créer 20 transactions
                for (int i = 0; i < 20; i++) {
                    Transaction t = new Transaction();
                    
                    // Alterner entre Roulette et Blackjack
                    boolean isRoulette = random.nextBoolean();
                    Room selectedRoom = isRoulette ? roomRoulette : roomBlackjack;
                    
                    // Générer un montant aléatoire (gain ou perte)
                    // Entre -50 et +100
                    double amountVal = -50 + (150 * random.nextDouble());
                    BigDecimal amount = BigDecimal.valueOf(amountVal).setScale(2, java.math.RoundingMode.HALF_UP);
                    
                    t.setMontant(amount);
                    
                    // Date : il y a 'i' jours (pour avoir un historique)
                    t.setDate(LocalDateTime.now().minusDays(20 - i).plusHours(random.nextInt(12)));
                    
                    t.setUser(adminUser);
                    t.setRoom(selectedRoom);
                    
                    // Mise à jour du solde virtuel pour que ce soit logique
                    currentBalance = currentBalance.add(amount);
                    
                    transactionRepository.save(t);
                }

            };
        };
    }
}
