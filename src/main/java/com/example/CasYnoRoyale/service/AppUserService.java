package com.example.CasYnoRoyale.service;


import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.repository.AppUserRepository;


import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;
    private final RoleService roleService;

    public AppUserService(AppUserRepository userRepository,RoleRepository roleRepository, RoleService roleService){
        this.userRepository = userRepository;
        this.roleService = roleService;

    }

    @PostConstruct
    @Transactional
    public void initTestUser(){
        AppUser u = userRepository.findByUsername("bernard111");
        if (u == null) {
            u = new AppUser();
            u.setRole(roleService.getUserRole());
            u.setName("Bernard");
            u.setUsername("bernard111");
            u.setPassword("password");
            
                    System.out.println("creation de bernard");

           
        }
        else System.out.println("Bernard existe déjà");
        u.setBalance(new BigDecimal(100));
        userRepository.save(u);
    }

    public AppUser getUserTest(){
        return userRepository.findByUsername("bernard111");
    }

}
