package service;

import model.User;
import dao.UserDao;
import java.sql.SQLException;
import java.util.Optional;

public class UserProfileService {
    private final UserDao userDao = new UserDao();

    /**
     * Retrieves the complete user profile data.
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User object.
     */
    public Optional<User> getUserProfile(int userId) {
        try {
            return userDao.findById(userId);
        } catch (SQLException e) {
            System.err.println("SQL Error during profile fetch: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Updates the user's profile with new information.
     * Includes basic validation logic.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateProfile(int userId, String fullName, String email, String phoneNumber) {
        // Simple input validation
        if (userId <= 0 || fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            return false;
        }

        // Additional email format check (optional but recommended)
        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
            return false;
        }

        User userToUpdate = new User(userId, fullName.trim(), email.trim(), phoneNumber != null ? phoneNumber.trim() : null);

        try {
            return userDao.updateProfile(userToUpdate);
        } catch (SQLException e) {
            System.err.println("SQL Error during profile update: " + e.getMessage());
            // Specific handling for unique constraint violation (e.g., email already exists)
            // if (e.getSQLState().startsWith("23")) return false;
            return false;
        }
    }
}
