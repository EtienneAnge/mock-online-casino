package com.example.CasYnoRoyale.service;

import com.example.CasYnoRoyale.database.User;
import com.example.CasYnoRoyale.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    final private UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByUserName(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Username not found"));
    }
}
