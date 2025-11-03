package service.user;

import dao.user.UserDAO;
import model.user.User;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period; // Import thêm class Period
import java.util.regex.Pattern;

public class UserProfileService {
    private UserDAO userDAO;

    public UserProfileService() {
        this.userDAO = new UserDAO();
    }

    public User getUserProfile(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ValidationResult updateUserProfile(User user) {
        ValidationResult validation = validateUserData(user);

        if (!validation.isValid()) {
            return validation;
        }

        try {
            if (userDAO.isEmailExists(user.getEmail(), user.getUserId())) {
                validation.setMessage("Email is already in use by another account.");
                return validation;
            }

            boolean success = userDAO.updateUserProfile(user);

            if (success) {
                validation.setValid(true);
                validation.setMessage("Profile updated successfully.");
            } else {
                validation.setValid(false);
                validation.setMessage("An error occurred while updating the profile.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            validation.setValid(false);
            validation.setMessage("System error: " + e.getMessage());
        }

        return validation;
    }

    private ValidationResult validateUserData(User user) {
        ValidationResult result = new ValidationResult();

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            result.setMessage("Full name cannot be empty.");
            return result;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            result.setMessage("Email cannot be empty.");
            return result;
        }
        if (!isValidEmail(user.getEmail())) {
            result.setMessage("Invalid email format.");
            return result;
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!isValidPhone(user.getPhoneNumber())) {
                result.setMessage("Invalid phone number format (must be 10-11 digits).");
                return result;
            }
        }

        if (user.getBirthDate() != null) {
            LocalDate dob = user.getBirthDate().toLocalDate();
            int age = Period.between(dob, LocalDate.now()).getYears();
            if (age < 18) {
                result.setMessage("User must be at least 18 years old.");
                return result;
            }
            if (age > 100) {
                result.setMessage("Age cannot exceed 100 years.");
                return result;
            }
        }

        result.setValid(true);
        return result;
    }

    private boolean isValidBirthDate(Date birthDate) {
        if (birthDate == null) {
            return true;
        }
        LocalDate dob = birthDate.toLocalDate();
        LocalDate today = LocalDate.now();
        if (dob.isAfter(today)) {
            return false;
        }
        int age = Period.between(dob, today).getYears();
        return age >= 18 && age <= 100;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(0|\\+84)[0-9]{9,10}$";
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }

    public static class ValidationResult {
        private boolean valid = false;
        private String message = "";

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.valid = false; this.message = message; }
    }
}
