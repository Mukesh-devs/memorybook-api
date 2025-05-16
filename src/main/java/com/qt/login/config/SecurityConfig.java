package com.qt.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Allow access to your APIs
                .anyRequest().authenticated() // Secure other endpoints
            )
            .formLogin(form -> form.disable()) // Disable login form to avoid redirect
            .httpBasic(httpBasic -> httpBasic.disable()); // Disable basic auth

        return http.build();
    }
}
