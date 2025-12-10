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
}
