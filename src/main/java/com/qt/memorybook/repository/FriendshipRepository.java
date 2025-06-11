package com.qt.memorybook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; // Import the composite key
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qt.memorybook.model.Friendship;
import com.qt.memorybook.model.FriendshipId;
import com.qt.memorybook.model.User;

@Repository
// FIX: The ID type is FriendshipId, not Long
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

    // Find all accepted friendships for a given user
    @Query("SELECT f FROM Friendship f WHERE (f.userOne = :user OR f.userTwo = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);
    
    // Find all pending friend requests where the given user is the recipient
    @Query("SELECT f FROM Friendship f WHERE (f.userOne = :user OR f.userTwo = :user) AND f.status = 'PENDING' AND f.actionUser != :user")
    List<Friendship> findPendingFriendRequests(@Param("user") User user);
    
    // Find a specific friendship between two users
    @Query("SELECT f FROM Friendship f WHERE (f.userOne = :user1 AND f.userTwo = :user2) OR (f.userOne = :user2 AND f.userTwo = :user1)")
    Optional<Friendship> findFriendshipBetween(@Param("user1") User user1, @Param("user2") User user2);
}
