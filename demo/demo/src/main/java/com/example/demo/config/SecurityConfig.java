package com.example.demo.config; // package declaration for the configuration class

import org.springframework.beans.factory.annotation.Autowired; // for injecting beans by type
import org.springframework.context.annotation.Bean; // marks methods that produce beans
import org.springframework.context.annotation.Configuration; // marks this class as a configuration class
import org.springframework.security.authentication.AuthenticationManager; // manages authentication
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // used to obtain AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // enables method-level security
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // builder for web security configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // enables web security
import org.springframework.security.config.http.SessionCreationPolicy; // defines session creation policies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCrypt password encoder implementation
import org.springframework.security.crypto.password.PasswordEncoder; // password encoder interface
import org.springframework.security.web.SecurityFilterChain; // defines the filter chain bean
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // standard username/password filter class

@Configuration // marks this class as a source of bean definitions
@EnableWebSecurity // enables Spring Security's web security support
@EnableMethodSecurity(prePostEnabled = true) // allows use of @PreAuthorize and @PostAuthorize on methods
public class SecurityConfig { // security configuration class

    @Autowired // inject JwtFilter bean (by type)
    private JwtFilter jwtFilter; // custom filter to validate JWT tokens on incoming requests

    @Bean // exposes SecurityFilterChain as a bean for Spring Security configuration
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // configure HttpSecurity
        http
                // disable CSRF for APIs (stateless) - CSRF protection usually not needed for token-based APIs
                .csrf(csrf -> csrf.disable()) // disable CSRF protection
                // authorize requests - define which endpoints are public and which require auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate", "/register").permitAll() // allow unauthenticated access to auth endpoints
                        .anyRequest().authenticated() // require authentication for any other request
                )
                // stateless session - do not create HTTP session for storing authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // set session policy to STATELESS

        // add JWT filter before UsernamePasswordAuthenticationFilter - ensures JWTs are validated before username/password processing
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // register custom JWT filter in the filter chain

        return http.build(); // build and return the configured SecurityFilterChain
    }

    // AuthenticationManager bean
    @Bean // expose AuthenticationManager so it can be injected elsewhere (e.g., auth controller)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // obtain from AuthenticationConfiguration
        return config.getAuthenticationManager(); // return the configured AuthenticationManager
    }



    // Password encoder bean
    @Bean // expose PasswordEncoder as a bean for encoding and matching passwords
    public PasswordEncoder passwordEncoder() { // create and return a BCryptPasswordEncoder
        return new BCryptPasswordEncoder(); // BCrypt is a strong hashing algorithm for passwords
    }

}
//package com.example.demo.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true) // enables @PreAuthorize
//public class SecurityConfig {
//
//    @Autowired
//    private JwtFilter jwtFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // disable CSRF for APIs (stateless)
//                .csrf(csrf -> csrf.disable())
//                // authorize requests
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/authenticate", "/register").permitAll()
//                        .anyRequest().authenticated()
//                )
//                // stateless session
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        // add JWT filter before UsernamePasswordAuthenticationFilter
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    // AuthenticationManager bean
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//
//
//    // Password encoder bean
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//}
