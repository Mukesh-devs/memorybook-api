package com.qt.memorybook.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
 
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
    String idToken = payload.get("token");

    try {
        // Verify ID token
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String userId = decodedToken.getUid();
        String email = decodedToken.getEmail();
        
        // You can use the `userId` and `email` to log the user in or register the user in your database.
        
        // Send back the response after successful authentication
        Map<String, String> response = Collections.singletonMap("message", "Google login successful! User: " + email);
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
    }
}

}
