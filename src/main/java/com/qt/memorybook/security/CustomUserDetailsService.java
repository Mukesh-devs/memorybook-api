package com.qt.memorybook.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qt.memorybook.model.User;
import com.qt.memorybook.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Find by username first
        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        // If not found by username, try by email.
        // We retrieve the User object first and then return it to avoid type inference issues.
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)
                );
        return user;
    }

    // This method is used by the JWT filter to load a user by ID
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        // We make the return explicit to help the compiler.
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id: " + id)
        );
        return user;
    }
}
