package com.example.CasYnoRoyale.repository;

import com.example.CasYnoRoyale.database.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
