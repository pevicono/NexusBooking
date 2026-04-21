package com.example.nexusbooking.service;

import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<User> findNonAdminUsers() {
        return userRepository.findNonAdminUsers();
    }

    public User updateUser(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail) && userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User setActive(User user, boolean active) {
        user.setActive(active);
        return userRepository.save(user);
    }

    public User createUserWithRole(String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        try {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role: " + role);
        }
        return userRepository.save(user);
    }

    public User changeRole(Long userId, String newRole) {
        User user = findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        try {
            user.setRole(User.Role.valueOf(newRole.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role: " + newRole);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}