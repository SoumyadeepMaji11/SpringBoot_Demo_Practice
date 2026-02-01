package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired private AuthenticationManager authManager;
    @Autowired
    private CustomUserDetailsService service;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        UserDetails details = service.loadUserByUsername(req.getUsername());
        String token = jwtUtil.generateToken(details);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
