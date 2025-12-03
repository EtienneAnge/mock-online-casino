package com.example.CasYnoRoyale.service;


import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import com.example.CasYnoRoyale.service.RoleService;


import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public AppUserService(AppUserRepository userRepository,RoleRepository roleRepository, RoleService roleService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;

    }

    @PostConstruct
    public void initTestUser(){
        AppUser u = userRepository.findByUsername("bernard111");
        if (u == null) {
            u = new AppUser();
            u.setRole(roleService.getUserRole());
            u.setName("Bernard");
            u.setUsername("bernard111");
            u.setPassword("password");
            
                    

           
        }
        u.setBalance(new BigDecimal(100));
        userRepository.save(u);
    }

    public AppUser getUserTest(){
        return userRepository.findByUsername("bernard111");
    }

}
