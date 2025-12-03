package com.example.CasYnoRoyale.database;



import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoles;

    private Integer level;
    private String label;

    @OneToMany(mappedBy = "role")
    @ToString.Exclude
    private List<AppUser> users;
}