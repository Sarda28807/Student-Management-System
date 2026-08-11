package com.novastudent.service;

import com.novastudent.dao.UserDAO;
import com.novastudent.model.User;
import com.novastudent.security.PasswordHasher;
import com.novastudent.security.SessionManager;

import java.sql.SQLException;

/**
 * Service layer for user authentication.
 * Handles login verification, session management, and password operations.
 */
public class AuthenticationService {

    private final UserDAO userDAO;
    private final SessionManager sessionManager;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username The username
     * @param password The plain-text password
     * @return The authenticated User, or null if authentication fails
     * @throws SQLException if database error occurs
     */
    public User authenticate(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            return null;
        }

        if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            sessionManager.setCurrentUser(user);
            return user;
        }

        return null;
    }

    /**
     * Changes the password for the current user.
     *
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password was changed successfully
     */
    public boolean changePassword(String currentPassword, String newPassword) throws SQLException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        if (!PasswordHasher.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
            return false;
        }

        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }

        String newHash = PasswordHasher.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(currentUser.getId(), newHash);
        if (updated) {
            currentUser.setPasswordHash(newHash);
        }
        return updated;
    }

    /**
     * Creates the initial admin account if none exists.
     */
    public boolean setupInitialAdmin(String username, String password, String fullName) throws SQLException {
        if (userDAO.adminExists()) {
            return false;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(PasswordHasher.hashPassword(password));
        admin.setFullName(fullName);
        admin.setRole(User.Role.ADMIN);
        admin.setEmail(username + "@novastudent.edu");

        return userDAO.insert(admin) > 0;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        sessionManager.logout();
    }

    /**
     * Checks if any admin exists in the system.
     */
    public boolean hasAdmin() throws SQLException {
        return userDAO.adminExists();
    }
}
