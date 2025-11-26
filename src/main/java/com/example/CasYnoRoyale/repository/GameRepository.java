package com.example.CasYnoRoyale.repository;

import com.example.CasYnoRoyale.database.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}