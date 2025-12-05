package com.example.CasYnoRoyale.repository;



import com.example.CasYnoRoyale.database.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t " + 
       "JOIN t.room r " + 
       "JOIN r.users u " + 
       "WHERE u.idUser = :userId AND r.game.idGame IN :gameIds")
List<Transaction> findTransactionsByUserAndGames(
    @Param("userId") Long userId, 
    @Param("gameIds") List<Long> gameIds
);
}