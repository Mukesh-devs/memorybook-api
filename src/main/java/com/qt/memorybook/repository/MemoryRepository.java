package com.qt.memorybook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qt.memorybook.model.Memory;
import com.qt.memorybook.model.User;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {

    // This is the core logic for the feed. It finds all memories where the user
    // is either the author OR they are present in the set of tagged users.
    // The results are ordered by the most recent first.
    @Query("SELECT m FROM Memory m WHERE m.author = :user OR :user MEMBER OF m.taggedUsers ORDER BY m.createdAt DESC")
    List<Memory> findFeedForUser(@Param("user") User user);

    // @Query("SELECT m FROM Memory m WHERE m.author = :user OR :user MEMBER OF m.taggedUsers ORDER BY m.createdAt DESC")
    // Page<Memory> findFeedForUser(@Param("user") User user, Pageable pageable);

}
