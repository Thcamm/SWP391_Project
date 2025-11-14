package service.employee;

import dao.user.UserDAO;
import dao.employee.admin.AdminDAO;
import model.user.User;
import model.employee.admin.UserDisplay;
import model.employee.admin.rbac.Role;
import dao.employee.admin.rbac.RoleDao;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class AdminService {

    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private RoleDao roleDao;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        this.roleDao = new RoleDao();
    }

    public Role getRoleById(int roleId) throws SQLException {
        return roleDao.findById(roleId);
    }

    private int getCreatedByEmployeeId(String userName) throws SQLException {
        Integer employeeId = adminDAO.getEmployeeIdByUsername(userName);
        return employeeId != null ? employeeId : 1;
    }

    public boolean promoteCustomerToEmployee(int userId, String newRoleName,
            String employeeCode, double salary,
            int managedByEmployeeId, String currentUser) {

        try {
            int createdByEmployeeId = getCreatedByEmployeeId(currentUser);

            return adminDAO.executePromoteToEmployeeSP(
                    userId, newRoleName, employeeCode, salary,
                    managedByEmployeeId, createdByEmployeeId);

        } catch (SQLException e) {
            System.err.println("SERVICE Lỗi thăng cấp Customer -> Employee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserBasicInfo(int userId, String fullName, String email, int roleId, boolean activeStatus, String currentUser) {
        try {
            User user = userDAO.getUserById(userId);

            if (user == null)
                return false;

            user.setFullName(fullName);
            user.setEmail(email);
            user.setRoleId(roleId);
            user.setActiveStatus(activeStatus);

            return adminDAO.updateUserBasicInfo(user);
        } catch (SQLException e) {
            System.err.println("SERVICE Lỗi cập nhật user cơ bản: " + e.getMessage());
            return false;
        }
    }

    public boolean isAdmin(String userName) {
        try {

            if (userName == null || userName.trim().isEmpty()) {
                return false;
            }

            User user = userDAO.getUserByUserName(userName);
            System.out.println("User found: " + (user != null));

            if (user == null) {
                return false;
            }

            System.out.println("User ID: " + user.getUserId());
            System.out.println("User Name: " + user.getUserName());
            System.out.println("User Role ID: " + user.getRoleId());
            System.out.println("User Active Status: " + user.isActiveStatus());

            if (!user.isActiveStatus()) {
                return false;
            }

            // USE EXISTING findById() method
            boolean hasAdminPermission = checkAdminPermissionByRole(user.getRoleId());

            if (hasAdminPermission) {

            } else {
                System.out.println(" User DOES NOT HAVE ADMIN permission - Access DENIED");
            }

            return hasAdminPermission;

        } catch (SQLException e) {
            System.err.println(" ERROR checking admin privileges: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check admin permission by role name/characteristics
     */
    private boolean checkAdminPermissionByRole(int roleId) {
        try {
            System.out.println("Checking role permissions for roleId: " + roleId);

            Role role = roleDao.findById(roleId);

            if (role != null) {
                String roleName = role.getRoleName().toLowerCase();
                boolean isAdminRole = roleName.contains("admin") ||
                        roleName.contains("manager") ||
                        roleName.contains("supervisor") ||
                        roleName.equals("tech manager") ||
                        roleName.contains("quản lý") || // Vietnamese
                        roleName.contains("admin"); // Any admin variant
                return isAdminRole;

            } else {
                System.out.println(" Role not found for roleId: " + roleId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println(" Error checking role permission: " + e.getMessage());
            e.printStackTrace();

            // FALLBACK: Use original hardcoded logic if database fails
            System.out.println(" Using fallback logic - checking roleId: " + roleId);
            boolean fallbackResult = roleId == 1 || roleId == 2; // Admin or Manager
            System.out.println("Fallback result: " + fallbackResult);
            return fallbackResult;
        }
    }

    /**
     * ALTERNATIVE: Completely bypass permission check for development
     */
    public boolean isAdminForDevelopment(String userName) {
        try {
            User user = userDAO.getUserByUserName(userName);
            if (user != null && user.isActiveStatus()) {
                System.out.println(" DEVELOPMENT MODE: Granting admin access to: " + userName);
                return true; // Allow any active user for development
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error in development admin check: " + e.getMessage());
            return false;
        }
    }


    /**
     * Create user with optional employee details for non-customer roles
     */
    // THAY ĐỔI: Kiểu trả về là String
    public String createUser(String fullName, String userName, String email, int roleId, String gender,
                             String currentUser, String employeeCode, Double salary) {

        // 1. Tạo mật khẩu ngẫu nhiên ngay từ đầu
        String randomPassword = PasswordUtil.generateRandomPassword(6);

        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return null;
            }

            User existingUser = userDAO.getUserByUserName(userName);
            if (existingUser != null) {
                System.out.println("Username đã tồn tại: " + userName);
                return null;
            }

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUserName(userName);
            newUser.setEmail(email);
            newUser.setGender(gender);
            newUser.setRoleId(roleId);
            newUser.setPasswordHash(PasswordUtil.hashPassword(randomPassword));
            newUser.setActiveStatus(true);
            Integer createdByEmployeeId = null;
            createdByEmployeeId = adminDAO.getEmployeeIdByUsername(currentUser);

            // Use AdminDAO's enhanced createUser method
            boolean success = adminDAO.createUser(newUser, employeeCode, salary, createdByEmployeeId);

            if (success) {
                System.out.println("User " + currentUser + " đã tạo user mới: " + userName);

                if (isEmployeeRole(roleId)) {
                    System.out.println("Employee record also created for non-customer user: " + userName);
                }
                return randomPassword;
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo user: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if role requires Employee record (non-Customer roles)
     */
    private boolean isEmployeeRole(int roleId) {
        try {
            Role role = roleDao.findById(roleId);
            if (role != null) {
                String roleName = role.getRoleName().toLowerCase();
                return !roleName.equals("customer") && !roleName.equals("khách hàng");
            }
        } catch (SQLException e) {
            System.err.println("Error checking role type: " + e.getMessage());
        }
        return false; // Default to false if unable to determine
    }

    /**
     * Get all available roles for user creation
     */
    public ArrayList<Role> getAvailableRoles() {
        try {
            return (ArrayList<Role>) roleDao.findAll();
        } catch (Exception e) {
            System.err.println("Error retrieving available roles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public common.utils.PaginationUtils.PaginationResult<UserDisplay> searchUsersWithPagination(
            String keyword, Integer roleId, Boolean activeStatus, String sortBy,
            int currentPage, int itemsPerPage) {
        try {
            // Get all results first
            ArrayList<UserDisplay> allResults = adminDAO.searchAllUsersWithRole(keyword, roleId, activeStatus, sortBy);

            // Apply pagination
            common.utils.PaginationUtils.PaginationResult<UserDisplay> paginatedResult = common.utils.PaginationUtils
                    .paginate(allResults, currentPage, itemsPerPage);

            System.out.println(" Pagination: Page " + currentPage + "/" + paginatedResult.getTotalPages() +
                    " - Showing " + paginatedResult.getPaginatedData().size() + "/" + paginatedResult.getTotalItems());

            return paginatedResult;
        } catch (SQLException e) {
            System.err.println("ADMIN Error searching users with pagination: " + e.getMessage());
            e.printStackTrace();
            return new common.utils.PaginationUtils.PaginationResult<>(new ArrayList<>(), 0, 0, 1, itemsPerPage);
        }
    }

    public int getActiveUsersCount() {
        try {
            return adminDAO.countSearchResults(null, null, true);
        } catch (SQLException e) {
            System.err.println("Error counting active users: " + e.getMessage());
            try {
                List<User> users = userDAO.getAllActiveUsers();
                return (int) users.stream().filter(User::isActiveStatus).count();
            } catch (SQLException fallbackError) {
                return 0;
            }
        }
    }

    public int getInactiveUsersCount() {
        try {
            return adminDAO.countSearchResults(null, null, false);
        } catch (SQLException e) {
            System.err.println("Error counting inactive users: " + e.getMessage());
            return 0;
        }
    }

    public int getAdminUsersCount() {
        try {
            // Get all users and count those with admin roles (dynamic approach)
            ArrayList<UserDisplay> allUsers = adminDAO.searchAllUsersWithRole(null, 1, null, null);
            int adminCount = 0;

            for (UserDisplay user : allUsers) {
                if (checkAdminPermissionByRole(user.getRoleId())) {
                    adminCount++;
                }
            }

            System.out.println(" Found " + adminCount + " admin users (dynamic count)");
            return adminCount;

        } catch (SQLException e) {
            System.err.println("Error counting admin users: " + e.getMessage());
            try {
                List<User> users = userDAO.getAllActiveUsers();
                return (int) users.stream().filter(user -> checkAdminPermissionByRole(user.getRoleId())).count();
            } catch (SQLException fallbackError) {
                return 0;
            }
        }
    }

    // ===== User Detail =====

    /**
     * GET USER BY ID: Lấy thông tin chi tiết user theo ID
     */
    public UserDisplay getUserById(int userId) {
        try {
            UserDisplay user = adminDAO.getUserDisplayById(userId);

            if (user != null) {
                System.out.println("User found: " + user.getUserName());
            } else {
                System.out.println(" User not found for ID: " + userId);
            }

            return user;
        } catch (SQLException e) {
            System.err.println(" Error getting user by ID " + userId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * TOGGLE USER STATUS
     */
    public boolean toggleUserStatus(int userId, boolean newStatus, String currentUser) {
        try {
            System.out.println(" AdminService.toggleUserStatus() called");
            System.out.println("    User ID: " + userId);
            System.out.println("    New Status: " + (newStatus ? "ACTIVE" : "INACTIVE"));
            System.out.println("    Actor: " + currentUser);

            boolean success = adminDAO.updateUserStatus(userId, newStatus, currentUser);

            if (success) {
                System.out.println(" User status updated successfully");
            } else {
                System.out.println(" Failed to update user status");
            }

            return success;
        } catch (SQLException e) {
            System.err.println(" Error toggling user status for ID " + userId + ": " + e.getMessage());
            return false;
        }
    }

    // ===== DUPLICATE CHECK METHODS =====

    /**
     * Check if username already exists in database
     * 
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        try {
            User existingUser = userDAO.getUserByUserName(username);
            return existingUser != null;
        } catch (SQLException e) {
            System.err.println("Error checking username duplicate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if email already exists in database
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        try {
            User existingUser = userDAO.getUserByEmail(email);
            return existingUser != null;
        } catch (SQLException e) {
            System.err.println("Error checking email duplicate: " + e.getMessage());
            return false;
        }
    }

    // ===== HELPER METHODS =====
}