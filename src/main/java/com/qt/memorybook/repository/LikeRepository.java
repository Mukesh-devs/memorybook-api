package com.qt.memorybook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qt.memorybook.model.Like;
import com.qt.memorybook.model.Memory;
import com.qt.memorybook.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndMemory(User user, Memory memory);
}
