package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.AppUser;
import com.example.CasYnoRoyale.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    final private AppUserRepository appUserRepository;
    public UserService(AppUserRepository userRepository){
        this.appUserRepository = userRepository;
    }

    public AppUser findByUserName(String username){
        return appUserRepository.findByUsername(username);
    }
}
