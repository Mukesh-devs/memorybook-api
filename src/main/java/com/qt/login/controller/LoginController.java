package com.qt.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.qt.login.model.User;
import com.qt.login.repository.UserRepository;


@Controller
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

    @GetMapping("/login")
    public String loginForm() {
        return "index";
    }
    @GetMapping("/register")
    public String registerForm() {
        return "index";
    }

    @PostMapping("/register")
    public String Register(@RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String repassword,
                                Model model){

        if ( registerUser(email, password,repassword)) {
            return "dashboard";
        }
        else {
            model.addAttribute("error",password.equals(repassword) ? "UserName is already exists" : "Passwords do not match");
            return "index";
        }
    }
    
    
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              Model model) {

        System.out.println("Username: " + email);
        System.out.println("Password: " + password);

        if (authenticate(email, password)) {
            return "dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "index";
        }
    }
}
