package com.example.CasYnoRoyale.database;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String name;
    private String username;
    private BigDecimal solde;

    @ManyToOne
    @JoinColumn(name = "idRoles", nullable = false)
    private Role role;

  
    @ManyToMany
    @JoinTable(
        name = "UsersRooms",                         
        joinColumns = @JoinColumn(name = "idUser"),  
        inverseJoinColumns = @JoinColumn(name = "idRoom") 
    )
    private List<Room> rooms; 

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;


    /**
     * Augmente le solde de l'utilisateur du montant spécifié.
     * @param amount Le montant à ajouter. Doit être positif.
     */
    public void increaseSolde(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à ajouter doit être positif.");
        }
        this.solde = this.solde.add(amount);
    }
    
    /**
     * Réduit le solde de l'utilisateur du montant spécifié.
     * (Vérification de solde insuffisant incluse)
     * @param amount Le montant à retirer. Doit être positif.
     */
    public void decreaseSolde(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à retirer doit être positif.");
        }
        
        if (this.solde.compareTo(amount) < 0) {
             throw new IllegalStateException("Solde insuffisant.");
        }
        
        this.solde = this.solde.subtract(amount);
    }
}