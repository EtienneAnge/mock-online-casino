package com.example.CasYnoRoyale.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String name;
    private String username;
    private String password;
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "idRoles", nullable = false)
    @ToString.Exclude
    private Role role;

    @ManyToMany
    @JoinTable(name = "UsersRooms", joinColumns = @JoinColumn(name = "idUser"), inverseJoinColumns = @JoinColumn(name = "idRoom"))
    @ToString.Exclude
    private List<Room> rooms;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Transaction> transactions;

}