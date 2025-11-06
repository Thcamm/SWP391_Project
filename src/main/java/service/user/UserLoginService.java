package service.user;

import dao.user.UserDAO;
import model.employee.Employee;
import model.user.User;
import java.sql.SQLException;

public class UserLoginService {
    private UserDAO userDAO;

    public UserLoginService(UserDAO userDao) {
        this.userDAO = userDao;
    }

    public UserLoginService() {
    }

    public User login(String userName, String password) {
        try {
            User user = userDAO.getUserByUserName(userName);
            if (user != null && verifyPassword(password, user.getPasswordHash()) && user.isActiveStatus()) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or handle as needed
        }
        return null;
    }

    public boolean verifyPassword(String rawPassword, String passwordHash) {
        // For demo: compare plain text. Replace with hash check in production.
        return rawPassword != null && passwordHash != null && rawPassword.equals(passwordHash);
    }

    public User findByUserName(String userName) {
        try {
            return userDAO.getUserByUserName(userName);
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or handle as needed
            return null;
        }
    }

    public Employee findEmployeeByUserName(String userName) {
        return userDAO.getEmployeeByUserName(userName);
    }
}
