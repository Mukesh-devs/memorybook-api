package com.qt.login.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qt.login.model.User;
import com.qt.login.repository.UserRepository;
// import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge= 3600)
public class LoginController {
    
    @Autowired
    private UserRepository userRepository;

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


    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody Map<String,String> user ){
        String email = user.get("email");
        String password = user.get("password");
        String repassword = user.get("repassword");

        if ( userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("User Already Exists.."));
        }

        if ( !password.equals(repassword) ){
            // model.addAttribute("error",password.equals(repassword) ? "UserName is already exists" : "Passwords do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords does not match..");
        }
            User newuser = new User();
            newuser.setEmail(email);
            newuser.setPassword(password);
            userRepository.save(newuser);

            return ResponseEntity.ok("User registered Successfully..");

    }
    
    
    @PostMapping("/login")
    public ResponseEntity<String> loginSubmit(@RequestBody Map<String, String> user) {

        String email = user.get("email");
        String password = user.get("password");

        System.out.println("Username: " + email);
        System.out.println("Password: " + password);

        if (authenticate(email, password)) {
            return ResponseEntity.ok("Login Successfully..");
        } 
        else {
            // model.addAttribute("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials..");
        }
    }
}
