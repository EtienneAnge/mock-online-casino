package com.example.CasYnoRoyale.database;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
public class Transaction {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idTrans;

    private BigDecimal montant;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "idGame", nullable = false)
    private Game game;
}