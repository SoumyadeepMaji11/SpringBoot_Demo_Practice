package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that runs once per request to extract a JWT from the
 * Authorization header, validate it and set the authenticated user
 * in Spring Security's SecurityContext if the token is valid.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    // Utility for working with JWTs (extract username, validate token, etc.)
    @Autowired
    private JwtUtil jwtUtil;

    // Service to load user details (used to build Authentication when token is valid)
    @Autowired
    private CustomUserDetailsService service;

    /**
     * Extract the Authorization header, parse the JWT, validate it and
     * set the corresponding Authentication in the SecurityContext.
     * <p>
     * Steps:
     * 1. Read the Authorization header.
     * 2. If it starts with "Bearer ", extract the token and username.
     * 3. If username exists and no authentication is present in context:
     * a. Load UserDetails for the username.
     * b. Validate the token against the loaded user details.
     * c. If valid, create and set a UsernamePasswordAuthenticationToken.
     * 4. Continue the filter chain.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        // Read the Authorization header from the incoming request
        String header = req.getHeader("Authorization");
        String username = null;
        String token = null;

        // Typical header format: "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {
            // Remove the "Bearer " prefix to get the raw token
            token = header.substring(7);
            // Extract username (or subject) from the token
            username = jwtUtil.extractUsername(token);
        }

        // If we retrieved a username and there is no existing Authentication
        // in the SecurityContext, attempt to validate the token and authenticate.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details (authorities, credentials not used here)
            UserDetails details = service.loadUserByUsername(username);
            // Validate the token (checks signature, expiration, and optionally claims)
            if (jwtUtil.validateToken(token, details)) {
                // Build an Authentication object containing the user principal and authorities
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                // Set the authenticated user into the SecurityContext for downstream use
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Continue with the filter chain (important to always call this)
        chain.doFilter(req, res);
    }
}
