package com.qt.memorybook.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "friendships")
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userOneId")
    private User userOne;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userTwoId")
    private User userTwo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    // The user who initiated the last action (e.g., sent the request)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_user_id", nullable = false)
    private User actionUser;
    
    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        BLOCKED
    }

    // Getters and Setters...
    public FriendshipId getId() { return id; }
    public void setId(FriendshipId id) { this.id = id; }
    public User getUserOne() { return userOne; }
    public void setUserOne(User userOne) { this.userOne = userOne; }
    public User getUserTwo() { return userTwo; }
    public void setUserTwo(User userTwo) { this.userTwo = userTwo; }
    public FriendshipStatus getStatus() { return status; }
    public void setStatus(FriendshipStatus status) { this.status = status; }
    public User getActionUser() { return actionUser; }
    public void setActionUser(User actionUser) { this.actionUser = actionUser; }
}
