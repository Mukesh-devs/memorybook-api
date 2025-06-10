package com.qt.memorybook.controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qt.memorybook.model.PasswordResetToken;
import com.qt.memorybook.model.User;
import com.qt.memorybook.repository.PasswordResetTokenRepository;
import com.qt.memorybook.repository.UserRepository;
import com.qt.memorybook.security.JwtTokenProvider;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge= 3600)
public class LoginController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password).isPresent();
    }

    public boolean registerUser(String email, String password,String repassword) {
        if ( userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        if ( password.equals(repassword) ) {
            User newuser = new User();

            newuser.setEmail(email);
            newuser.setPassword(password);
            userRepository.save(newuser);
            return true;
        }
        else {
            return false;
        }

    }
    // @Autowired
    // private UserService userService;

    // @GetMapping("/login")
    // public String loginForm() {
    //     return "index";
    // }
    // @GetMapping("/register") 
    // public String registerForm() {
    //     return "index"; 
    // }

    
    // @GetMapping("/login")
    // public String loginPage() {
    //     return  
    // }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.get("username"), // Can be username or email
                        loginRequest.get("password")
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(Collections.singletonMap("accessToken", jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.get("username")).isPresent()) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(signUpRequest.get("email")).isPresent()) {
            return new ResponseEntity<>("Email Address already in use!", HttpStatus.BAD_REQUEST);
        }

        if(!signUpRequest.get("password").equals(signUpRequest.get("repassword"))){
            return new ResponseEntity<>("Passwords do not match!", HttpStatus.BAD_REQUEST);
        }

        // Create user's account
        User user = new User();
        user.setUsername(signUpRequest.get("username"));
        user.setEmail(signUpRequest.get("email"));
        user.setDisplayName(signUpRequest.get("username")); // Default display name
        
        // Encrypt the password
        user.setPassword(passwordEncoder.encode(signUpRequest.get("password")));
        
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (!userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 hour expiry
        tokenRepo.save(resetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the following link to reset your password:\n\n" + resetLink);

        mailSender.send(message);

        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        PasswordResetToken resetToken = tokenRepo.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);

            tokenRepo.delete(resetToken);

            return ResponseEntity.ok("Password updated successfully.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
    }

}
