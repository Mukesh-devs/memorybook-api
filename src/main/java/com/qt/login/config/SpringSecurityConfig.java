package com.qt.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Allow all API endpoints
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) // Disable default login form
            .oauth2Login(oauth -> oauth
                .loginPage("/login") // Optional if using Google OAuth login
            );

        return http.build();
    }
}

