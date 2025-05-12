package com.qt.login.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qt.login.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
    
}
