package service.employee;

import dao.user.UserDAO;
import model.user.User;
import model.employee.Admin;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.security.SecureRandom;


public class AdminService {
    private UserDAO userDAO;

    public AdminService() {
        this.userDAO = new UserDAO();
    }

    public List<User> getAllUsers() {
        try {
            return userDAO.getAllActiveUsers();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<User> searchUsers(String keyword) {
        try {
            List<User> allUsers = userDAO.getAllActiveUsers();
            List<User> result = new ArrayList<>();

            if (keyword == null || keyword.trim().isEmpty()) {
                return allUsers;
            }

            String searchKeyword = keyword.toLowerCase();
            for (User user : allUsers) {
                if (user.getUserName().toLowerCase().contains(searchKeyword) ||
                        user.getEmail().toLowerCase().contains(searchKeyword) ||
                        user.getFullName().toLowerCase().contains(searchKeyword)) {
                    result.add(user);
                }
            }

            System.out.println("Tìm thấy " + result.size() + " users với từ khóa: " + keyword);
            return result;

        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm kiếm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 3. Lọc users theo role
     */
    public List<User> getUsersByRole(int roleId) {
        try {
            List<User> allUsers = userDAO.getAllActiveUsers();
            List<User> result = new ArrayList<>();

            for (User user : allUsers) {
                if (user.getRoleId() == roleId) {
                    result.add(user);
                }
            }

            System.out.println("Tìm thấy " + result.size() + " users có role " + roleId);
            return result;

        } catch (SQLException e) {
            System.out.println("Lỗi khi lọc theo role: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * TRANSACTION 1: Đổi role - NHẬN CURRENT USER LÀM PARAMETER
     */
    public boolean changeUserRole(int userId, int newRoleId, String currentUser) {
        try {
            // Kiểm tra current user có được truyền vào không
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return false;
            }

            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("Không tìm thấy user có ID: " + userId);
                return false;
            }

            if (currentUser.equals(user.getUserName())) {
                System.out.println("User " + currentUser + " không thể thay đổi role của chính mình!");
                return false;
            }

            int oldRole = user.getRoleId();
            user.setRoleId(newRoleId);
            boolean success = userDAO.updateUser(user);

            if (success) {
                System.out.println("User " + currentUser + " đã thay đổi role của " + user.getUserName() +
                        " từ " + oldRole + " thành " + newRoleId);
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Lỗi khi đổi role: " + e.getMessage());
            return false;
        }
    }

    //
    public boolean toggleUserStatus(int userId, String currentUser) {
        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return false;
            }

            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("Không tìm thấy user có ID: " + userId);
                return false;
            }

            if (currentUser.equals(user.getUserName())) {
                System.out.println("User " + currentUser + " không thể khóa tài khoản của chính mình!");
                return false;
            }

            boolean oldStatus = user.isActiveStatus();
            user.setActiveStatus(!oldStatus);

            boolean success = userDAO.updateUser(user);

            if (success) {
                String action = oldStatus ? "Lock" : "Unlock";
                System.out.println("User " + currentUser + " đã " + action + " user: " + user.getUserName());
            }

            return success;

        } catch (SQLException e) {
            System.out.println("loi thay doi trang thai " + e.getMessage());
            return false;
        }
    }

    /**
     * TRANSACTION 3: Reset password - NHẬN CURRENT USER LÀM PARAMETER
     */
    public String resetPassword(int userId, String currentUser) {
        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("ko co thong tin");
                return null;
            }

            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("ko thay co: " + userId);
                return null;
            }

            String newPassword = "Password123";
            user.setPasswordHash(newPassword);

            boolean success = userDAO.updateUser(user);

            if (success) {
                System.out.println("User " + currentUser + " đã reset mật khẩu cho user: " + user.getUserName());
                return newPassword;
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Lỗi khi reset mật khẩu: " + e.getMessage());
            return null;
        }
    }

    /**
     * TRANSACTION 4: Activate user - NHẬN CURRENT USER LÀM PARAMETER
     */
    public boolean activateUser(int userId, String currentUser) {
        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return false;
            }

            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("Không tìm thấy user có ID: " + userId);
                return false;
            }

            user.setActiveStatus(true);
            boolean success = userDAO.updateUser(user);

            if (success) {
                System.out.println("User " + currentUser + " đã kích hoạt user: " + user.getUserName());
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Lỗi khi kích hoạt user: " + e.getMessage());
            return false;
        }
    }

    //Create user
    public boolean createUser(String fullName, String userName, String email, int roleId, String currentUser) {
        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return false;
            }

            User existingUser = userDAO.getUserByUsername(userName);
            if (existingUser != null) {
                System.out.println("Username đã tồn tại: " + userName);
                return false;
            }

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUserName(userName);
            newUser.setEmail(email);
            newUser.setRoleId(roleId);
            newUser.setPasswordHash("123456");
            newUser.setActiveStatus(true);

            boolean success = userDAO.addUser(newUser);

            if (success) {
                System.out.println("User " + currentUser + " đã tạo user mới: " + userName);
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo user: " + e.getMessage());
            return false;
        }
    }

    public boolean isAdmin(String userName) {
        try {
            User user = userDAO.getUserByUsername(userName);
            if (user != null && user.isActiveStatus()) {
                // roleId 1 = ADMIN, roleId 2 = MANAGER
                return user.getRoleId() == 1 || user.getRoleId() == 2;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra quyền admin: " + e.getMessage());
            return false;
        }
    }

    public int countUsers() {
        try {
            return userDAO.getAllActiveUsers().size();
        } catch (SQLException e) {
            System.out.println("Lỗi khi đếm users: " + e.getMessage());
            return 0;
        }
    }

    public User getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy user: " + e.getMessage());
            return null;
        }
    }
}