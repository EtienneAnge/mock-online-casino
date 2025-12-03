package com.example.CasYnoRoyale.service;


import com.example.CasYnoRoyale.database.Role;
import com.example.CasYnoRoyale.repository.RoleRepository;
import com.example.CasYnoRoyale.database.User;
import com.example.CasYnoRoyale.repository.UserRepository;
import com.example.CasYnoRoyale.service.RoleService;


import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository,RoleRepository roleRepository, RoleService roleService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;

    }

    @PostConstruct
    public void initTestUser(){
        User u = userRepository.findByUsername("bernard111");
        if (u == null) {
            u = new User();
            u.setRole(roleService.getUserRole());
            u.setName("Bernard");
            u.setUsername("bernard111");
            u.setPassword("password");
            
                    

           
        }
        u.setBalance(new BigDecimal(100));
        userRepository.save(u);
    }

    public User getUserTest(){
        return userRepository.findByUsername("bernard111");
    }

}
