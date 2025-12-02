package com.example.CasYnoRoyale.service;


import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.repository.RoleRepository;



import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
    final RoleRepository roleRepository;
    Role admin;
    Role user;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles(){
        //roleRepository.deleteAll();
        Role admin = new Role();
        admin.setLabel("Admin");
        admin.setLevel(0);
        roleRepository.save(admin);
        this.admin = admin;

        Role user = new Role();
        user.setLabel("User");
        user.setLevel(10);
        roleRepository.save(user);
        this.user = user;
    }

    public Role getAdminRole(){
        return admin;
    }

    public Role getUserRole(){
        return user;
    }
}
