package com.example.CasYnoRoyale.database;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "Users")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String name;
    private String username;
    private String password;
    private BigDecimal balance = BigDecimal.ZERO;

    /*@ManyToOne
    @JoinColumn(name = "idRoles", nullable = false)
    private Role role;*/

   // @Column(nullable = false)
    private String role = "ROLE_USER";

    @ManyToMany
    @JoinTable(name = "UsersRooms", joinColumns = @JoinColumn(name = "idUser"), inverseJoinColumns = @JoinColumn(name = "idRoom"))
    private List<Room> rooms;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Dans AppUser.java, tu dois ajouter :
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}