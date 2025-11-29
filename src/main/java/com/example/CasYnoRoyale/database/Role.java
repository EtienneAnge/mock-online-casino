package com.example.CasYnoRoyale.database;



import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoles;

    private Integer level;
    private String label;

    /*@OneToMany(mappedBy = "role")
    private List<AppUser> users;*/
}