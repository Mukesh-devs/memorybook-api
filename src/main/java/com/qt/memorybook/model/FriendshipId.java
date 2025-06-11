package com.qt.memorybook.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

// This class defines the composite primary key for the Friendship entity.
// It is now a top-level public class in its own file.
@Embeddable
public class FriendshipId implements Serializable {
    private Long userOneId;
    private Long userTwoId;

    // Constructors
    public FriendshipId() {}

    public FriendshipId(Long userOneId, Long userTwoId) {
        // Ensure consistent ordering to prevent duplicate friendships (e.g., 1-2 and 2-1)
        if (userOneId < userTwoId) {
            this.userOneId = userOneId;
            this.userTwoId = userTwoId;
        } else {
            this.userOneId = userTwoId;
            this.userTwoId = userOneId;
        }
    }
    
    // Getters and Setters
    public Long getUserOneId() { return userOneId; }
    public void setUserOneId(Long userOneId) { this.userOneId = userOneId; }
    public Long getUserTwoId() { return userTwoId; }
    public void setUserTwoId(Long userTwoId) { this.userTwoId = userTwoId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(userOneId, that.userOneId) && Objects.equals(userTwoId, that.userTwoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userOneId, userTwoId);
    }
}
