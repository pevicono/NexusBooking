package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT u FROM User u WHERE u.role = 'USER' ORDER BY u.email")
    List<User> findNonAdminUsers();
}