package com.example.nexusbooking.service;

import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail) && userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        user.setEmail(newEmail);
        return userRepository.save(user);
    }
}