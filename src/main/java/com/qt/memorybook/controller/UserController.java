package com.qt.memorybook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qt.memorybook.dto.UserSummary;
import com.qt.memorybook.model.User;
import com.qt.memorybook.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // This endpoint will be secured by default by our SecurityConfig.
    // It allows a logged-in user to search for other users.
    @GetMapping("/search")
    public ResponseEntity<List<UserSummary>> searchUsers(@RequestParam("q") String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(query, query);
        
        List<UserSummary> userSummaries = users.stream()
                .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getDisplayName(), user.getProfilePictureUrl()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userSummaries);
    }
}
