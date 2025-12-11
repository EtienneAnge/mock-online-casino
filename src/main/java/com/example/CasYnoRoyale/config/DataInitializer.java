package com.example.CasYnoRoyale.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
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
import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Game;

@Configuration
public class DataInitializer {
   /**
    * Créer une salle (room) pour un jeu donné si elle n'existe pas déjà.
    * @param repo Le repository des salles (rooms).
    * @param game Le jeu pour lequel créer la salle.
    * @return La salle créée ou existante.
    */
    private Room createRoomIfNotFound(RoomRepository repo, Game game) {
        //Création de la room
        Room r = new Room();
        //Initialisation des attributs
        r.setGame(game);
        r.setDate(LocalDateTime.now());

        return repo.save(r);
    }

    /**
     * Initialisation des données au démarrage de l'application (Utilisateur ADMIN et une vingtaine de transactions).
     * @param roleRepository        Le repository des roles
     * @param userRepository        Le repository des utilisateurs
     * @param transactionRepository Le repository des transactions
     * @param gameRepository        Le repository des jeux
     * @param roomRepository        Le repository des salles
     * @return                      Le runner de commande pour l'initialisation des données.
     */
    @Bean
    public CommandLineRunner initRolesAndTransactions(RoleRepository roleRepository,
                                        AppUserRepository userRepository,
                                        TransactionRepository transactionRepository,
                                        GameRepository gameRepository,
                                        RoomRepository roomRepository) {

        // Le corps de la fonction à exécuter au démarrage
        return args -> {

            //Initialisation des rôles
            //Role User
            if (roleRepository.findByLabel("ROLE_USER") == null) {
                Role userRole = new Role();
                userRole.setLabel("ROLE_USER");
                userRole.setLevel(0);
                roleRepository.save(userRole);
                System.out.println("Rôle 'ROLE_USER' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_USER' déjà existant.");
            }

            //Role Admin
            if (roleRepository.findByLabel("ROLE_ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setLabel("ROLE_ADMIN");
                adminRole.setLevel(1);
                roleRepository.save(adminRole);
                System.out.println("Rôle 'ROLE_ADMIN' créé et inséré en BDD.");
            } else {
                System.out.println("Rôle 'ROLE_ADMIN' déjà existant.");
            }

            //Initialisation compte admin
            //Role Admin
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

            //Initialisation des jeux
            //Blackjack
            Game blackjack = gameRepository.findByLabel("Blackjack");
            if (blackjack == null) {
                blackjack = new Game();
                blackjack.setLabel("Blackjack");
                blackjack.setUrl("/games/blackjack");
                gameRepository.save(blackjack);

            }

            //Roulette
            Game roulette = gameRepository.findByLabel("Roulette");
            if (roulette == null) {
                roulette = new Game();
                roulette.setLabel("Roulette");
                roulette.setUrl("/games/roulette");
                gameRepository.save(roulette);

            }

            //Initialisation des rooms
            Room roomBlackjack = createRoomIfNotFound(roomRepository, blackjack);
            Room roomRoulette = createRoomIfNotFound(roomRepository, roulette);
            
            //initilisations des transactions de test
            if (transactionRepository.count() == 0) {
                Random random = new Random();

                //Création de 20 transactions aléatoires
                for (int i = 0; i < 20; i++) {
                    Transaction t = new Transaction();
                    
                    //Alterner entre Roulette et Blackjack
                    boolean isRoulette = random.nextBoolean();
                    Game selectedGame = isRoulette ? roulette : blackjack;
                    
                    //Génération d'un montant aleatoire entre -50 et 100
                    double amountVal = -50 + (150 * random.nextDouble());
                    // Arrondir à 2 décimales
                    BigDecimal amount = BigDecimal.valueOf(amountVal).setScale(2, java.math.RoundingMode.HALF_UP);
                    
                    //Initialisation des attributs
                    t.setMontant(amount);
                    t.setDate(LocalDateTime.now().minusDays(20 - i).plusHours(random.nextInt(12)));
                    t.setUser(adminUser);
                    t.setGame(selectedGame);
                    
                    //Enregistrement
                    transactionRepository.save(t);
                }
            };
        };
    }
}
