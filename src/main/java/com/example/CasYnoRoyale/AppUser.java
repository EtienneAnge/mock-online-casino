package com.example.CasYnoRoyale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data @Entity
public class AppUser {
    @Id
    private String username;

    @Column
    private String password;
}
