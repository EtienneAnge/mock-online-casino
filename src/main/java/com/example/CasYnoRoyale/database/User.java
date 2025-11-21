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
}