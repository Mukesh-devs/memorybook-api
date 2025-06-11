package com.qt.memorybook.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qt.memorybook.dto.UserSummary;
import com.qt.memorybook.model.Friendship;
import com.qt.memorybook.model.Friendship.FriendshipStatus;
import com.qt.memorybook.model.FriendshipId;
import com.qt.memorybook.model.User;
import com.qt.memorybook.repository.FriendshipRepository;
import com.qt.memorybook.repository.UserRepository;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @PostMapping("/request/{username}")
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String username) {
        User currentUser = getCurrentUser(userDetails);
        Optional<User> friendOptional = userRepository.findByUsername(username);

        if (friendOptional.isEmpty()) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        User friend = friendOptional.get();
        if (currentUser.getId().equals(friend.getId())) {
            return new ResponseEntity<>("You cannot add yourself as a friend.", HttpStatus.BAD_REQUEST);
        }
        
        // FIX: Handle the Optional return type
        Optional<Friendship> existingFriendship = friendshipRepository.findFriendshipBetween(currentUser, friend);
        if (existingFriendship.isPresent()) {
            return new ResponseEntity<>("A friendship or request already exists.", HttpStatus.BAD_REQUEST);
        }
        
        Friendship newFriendship = new Friendship();
        newFriendship.setId(new FriendshipId(currentUser.getId(), friend.getId()));
        newFriendship.setUserOne(currentUser.getId() < friend.getId() ? currentUser : friend);
        newFriendship.setUserTwo(currentUser.getId() < friend.getId() ? friend : currentUser);
        newFriendship.setStatus(FriendshipStatus.PENDING);
        newFriendship.setActionUser(currentUser);
        
        friendshipRepository.save(newFriendship);
        
        return ResponseEntity.ok("Friend request sent.");
    }

    @PostMapping("/accept/{username}")
    public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String username) {
        User currentUser = getCurrentUser(userDetails);
        Optional<User> friendOptional = userRepository.findByUsername(username);

        if (friendOptional.isEmpty()) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        User friend = friendOptional.get();
        
        // FIX: Handle the Optional return type
        Optional<Friendship> friendshipOpt = friendshipRepository.findFriendshipBetween(currentUser, friend);
        if (friendshipOpt.isEmpty()) {
             return new ResponseEntity<>("No pending request from this user.", HttpStatus.BAD_REQUEST);
        }

        Friendship friendship = friendshipOpt.get();
        if (friendship.getStatus() != FriendshipStatus.PENDING || friendship.getActionUser().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>("No pending request from this user.", HttpStatus.BAD_REQUEST);
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setActionUser(currentUser);
        friendshipRepository.save(friendship);

        return ResponseEntity.ok("Friend request accepted.");
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<UserSummary>> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Friendship> pendingFriendships = friendshipRepository.findPendingFriendRequests(currentUser);
        
        List<UserSummary> requesters = pendingFriendships.stream()
            .map(Friendship::getActionUser)
            .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getDisplayName(), user.getProfilePictureUrl()))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(requesters);
    }
    
    @GetMapping("")
    public ResponseEntity<List<UserSummary>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Friendship> acceptedFriendships = friendshipRepository.findAcceptedFriendships(currentUser);
        
        List<UserSummary> friends = acceptedFriendships.stream()
            .map(f -> f.getUserOne().equals(currentUser) ? f.getUserTwo() : f.getUserOne())
            .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getDisplayName(), user.getProfilePictureUrl()))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(friends);
    }
}
