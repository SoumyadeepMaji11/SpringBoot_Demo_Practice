package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public User(){}

    public User(String username, String password,String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
