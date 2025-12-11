package com.example.CasYnoRoyale.repository;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.database.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserOrderByDateAsc(AppUser user);
    List<Transaction> findByUserAndGame_IdGameOrderByDateAsc(AppUser user, Long gameId);
}