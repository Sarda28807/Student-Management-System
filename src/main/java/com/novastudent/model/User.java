package com.novastudent.model;

import java.time.LocalDateTime;

/**
 * Represents an authenticated user (admin or staff).
 * Maps to the 'users' table.
 */
public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** User role enum */
    public enum Role {
        ADMIN, STAFF
    }

    public User() {
        this.role = Role.STAFF;
        this.active = true;
    }

    public User(String username, String passwordHash, String fullName, Role role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Check if user has admin privileges.
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
