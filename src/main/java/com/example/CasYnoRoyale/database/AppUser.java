package com.example.CasYnoRoyale.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "Users")
public class AppUser {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idUser;

    private String name;

    @Column(name = "username", unique = true)
    private String username;
    private String password;
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "idRoles", nullable = false)
    private Role role;

    @ManyToMany
    @JoinTable(name = "UsersRooms", joinColumns = @JoinColumn(name = "idUser"), inverseJoinColumns = @JoinColumn(name = "idRoom"))
    private List<Room> rooms;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    @Override
    public String toString() {
        return null;
    }

    /**
     * Augmente le balance de l'utilisateur du montant spécifié.
     * 
     * @param amount Le montant à ajouter. Doit être positif.
     */
    public void increaseBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à ajouter doit être positif.");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Réduit le balance de l'utilisateur du montant spécifié.
     * (Vérification de balance insuffisant incluse)
     * 
     * @param amount Le montant à retirer. Doit être positif.
     */
    public void decreaseBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à retirer doit être positif.");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("balance insuffisant.");
        }

        this.balance = this.balance.subtract(amount);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}