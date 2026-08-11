package com.novastudent.security;

import com.novastudent.model.User;

/**
 * Manages the current user session.
 * Tracks the authenticated user throughout the application lifecycle.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private long loginTime;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the current authenticated user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
    }

    /**
     * Gets the current authenticated user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user is an admin.
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Returns login duration in milliseconds.
     */
    public long getSessionDuration() {
        if (!isLoggedIn()) return 0;
        return System.currentTimeMillis() - loginTime;
    }

    /**
     * Logs out the current user and clears the session.
     */
    public void logout() {
        this.currentUser = null;
        this.loginTime = 0;
    }

    /**
     * Resets the singleton (for testing).
     */
    public static synchronized void reset() {
        instance = null;
    }
}
