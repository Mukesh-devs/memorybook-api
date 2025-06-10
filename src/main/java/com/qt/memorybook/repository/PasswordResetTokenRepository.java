package com.qt.memorybook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qt.memorybook.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
