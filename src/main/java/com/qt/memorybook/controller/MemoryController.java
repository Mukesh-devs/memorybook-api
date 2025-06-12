package com.qt.memorybook.controller;

import com.qt.memorybook.model.*;
import com.qt.memorybook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/memories")
public class MemoryController {

    @Autowired private MemoryRepository memoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private LikeRepository likeRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // --- FIX: Complete implementation for createMemory ---
    @PostMapping
    public ResponseEntity<?> createMemory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Object> request) {
        User currentUser = getCurrentUser(userDetails);

        String content = (String) request.get("content");
        List<String> taggedUsernames = (List<String>) request.get("taggedUsernames");

        if (content == null || content.isBlank()) {
            return new ResponseEntity<>("Memory content cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        Memory memory = new Memory();
        memory.setAuthor(currentUser);
        memory.setContent(content);
        memory.setCreatedAt(Instant.now());

        if (taggedUsernames != null && !taggedUsernames.isEmpty()) {
            Set<User> taggedUsers = userRepository.findByUsernameIn(taggedUsernames);
            memory.setTaggedUsers(taggedUsers);
        }

        memoryRepository.save(memory);

        return new ResponseEntity<>("Memory created successfully", HttpStatus.CREATED);
    }

    // --- UPDATED FEED ENDPOINT ---
    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<Memory> feed = memoryRepository.findFeedForUser(currentUser);
        
        List<Map<String, Object>> feedResponse = feed.stream().map(memory -> {
            Map<String, Object> memoryMap = new LinkedHashMap<>();
            memoryMap.put("id", memory.getId());
            memoryMap.put("content", memory.getContent());
            memoryMap.put("createdAt", memory.getCreatedAt());
            memoryMap.put("author", memory.getAuthor().getUsername());
            memoryMap.put("taggedUsers", memory.getTaggedUsers().stream().map(User::getUsername).collect(Collectors.toList()));
            
            // --- ADD LIKE AND COMMENT INFO ---
            memoryMap.put("likeCount", memory.getLikes().size());
            memoryMap.put("isLikedByUser", memory.getLikes().stream().anyMatch(like -> like.getUser().equals(currentUser)));
            
            List<Map<String, String>> comments = memory.getComments().stream()
                .map(comment -> Map.of(
                    "author", comment.getAuthor().getUsername(),
                    "content", comment.getContent()
                )).collect(Collectors.toList());
            memoryMap.put("comments", comments);

            return memoryMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(feedResponse);
    }
    
    // --- NEW ENDPOINTS ---
    @PostMapping("/{memoryId}/like")
    public ResponseEntity<?> toggleLike(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long memoryId) {
        User currentUser = getCurrentUser(userDetails);
        Memory memory = memoryRepository.findById(memoryId).orElseThrow(() -> new RuntimeException("Memory not found"));

        Optional<Like> existingLike = likeRepository.findByUserAndMemory(currentUser, memory);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get()); // Unlike
        } else {
            Like newLike = new Like();
            newLike.setUser(currentUser);
            newLike.setMemory(memory);
            likeRepository.save(newLike); // Like
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memoryId}/comments")
    public ResponseEntity<?> addComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long memoryId, @RequestBody Map<String, String> request) {
        User currentUser = getCurrentUser(userDetails);
        Memory memory = memoryRepository.findById(memoryId).orElseThrow(() -> new RuntimeException("Memory not found"));
        String content = request.get("content");
        
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setAuthor(currentUser);
        newComment.setMemory(memory);
        newComment.setCreatedAt(Instant.now());
        
        commentRepository.save(newComment);
        
        return new ResponseEntity<>("Comment added", HttpStatus.CREATED);
    }
}
