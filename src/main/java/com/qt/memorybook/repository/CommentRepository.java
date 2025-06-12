package com.qt.memorybook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qt.memorybook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
