package com.example.CasYnoRoyale.repository;

import com.example.CasYnoRoyale.database.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByLabel(String label);
}
