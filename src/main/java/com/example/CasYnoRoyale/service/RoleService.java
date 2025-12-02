package com.example.CasYnoRoyale.service;


import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.repository.RoleRepository;



import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
    final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles(){
        Role admin = new Role();
        admin.setLabel("Admin");
        admin.setLevel(0);
        roleRepository.save(admin);

        Role user = new Role();
        user.setLabel("User");
        user.setLevel(10);
        roleRepository.save(user);
    }
}
