package service.employee;

import dao.user.UserDAO;
import dao.employee.admin.AdminDAO;
import model.user.User;
import model.employee.admin.UserDisplay;
import model.employee.admin.rbac.Role;
import dao.employee.admin.rbac.RoleDao;

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

    /**
     * ✅ FIXED: Check admin permission by role name instead of hardcoded IDs
     */
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

            // ✅ USE EXISTING findById() method
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
     * NEW METHOD: Check admin permission by role name/characteristics
     */
    private boolean checkAdminPermissionByRole(int roleId) {
        try {
            System.out.println("Checking role permissions for roleId: " + roleId);

            // Use your existing findById() method
            Role role = roleDao.findById(roleId);

            if (role != null) {
                String roleName = role.getRoleName().toLowerCase();
                System.out.println("Found role: " + roleName);

                // FLEXIBLE: Check by role name patterns
                boolean isAdminRole = roleName.contains("admin") ||
                        roleName.contains("manager") ||
                        roleName.contains("supervisor") ||
                        roleName.equals("tech manager") ||
                        roleName.contains("quản lý") || // Vietnamese
                        roleName.contains("admin"); // Any admin variant

                System.out.println("Role '" + roleName + "' is admin role: " + isAdminRole);
                return isAdminRole;

            } else {
                System.out.println(" Role not found for roleId: " + roleId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println(" Error checking role permission: " + e.getMessage());
            e.printStackTrace();

            // ✅ FALLBACK: Use original hardcoded logic if database fails
            System.out.println(" Using fallback logic - checking roleId: " + roleId);
            boolean fallbackResult = roleId == 1 || roleId == 2; // Admin or Manager
            System.out.println("Fallback result: " + fallbackResult);
            return fallbackResult;
        }
    }

    /**
     * ALTERNATIVE: Check specific permission (if you implement Permission system)
     */
    private boolean checkSpecificPermission(int roleId, String permissionCode) {
        try {
            // Example:
            // return rolePermissionDAO.hasPermission(roleId, permissionCode);

            System.out.println("Permission check not implemented yet - using fallback");
            return false; // For now, return false to use role name check

        } catch (Exception e) {
            System.err.println("Error checking specific permission: " + e.getMessage());
            return false;
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

    // ===== ALL OTHER EXISTING METHODS (unchanged) =====

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

    // ... (keep all other existing methods unchanged)

    public boolean createUser(String fullName, String userName, String email, int roleId, String currentUser) {
        try {
            if (currentUser == null || currentUser.trim().isEmpty()) {
                System.out.println("Lỗi: Không có thông tin user hiện tại!");
                return false;
            }

            User existingUser = userDAO.getUserByUserName(userName);
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

    // ===== SEARCH METHODS (unchanged) =====

    public ArrayList<UserDisplay> searchUsers(String keyword, Integer roleId, Boolean activeStatus) {
        try {
            System.out.println("🔍 DEBUG AdminService.searchUsers() called with:");
            System.out.println("   keyword: " + keyword);
            System.out.println("   roleId: " + roleId);
            System.out.println("   activeStatus: " + activeStatus);

            // 🧪 TEST: Try simple query first
            try {
                ArrayList<User> simpleUsers = adminDAO.getAllUsersForAdmin();
                System.out.println("🧪 DEBUG: Simple query returned " + simpleUsers.size() + " users");
            } catch (Exception e) {
                System.err.println("🚨 DEBUG: Simple query failed: " + e.getMessage());
            }

            ArrayList<UserDisplay> result = adminDAO.searchAllUsersWithRole(keyword, roleId, activeStatus, "userid");
            System.out.println("🔍 DEBUG AdminService.searchUsers() returned " + result.size() + " users");
            return result;
        } catch (SQLException e) {
            System.err.println("ADMIN Error searching users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // New method with sort parameter
    public ArrayList<UserDisplay> searchUsers(String keyword, Integer roleId, Boolean activeStatus, String sortBy) {
        try {
            System.out.println("🔍 DEBUG AdminService.searchUsers() with sort called");
            System.out.println("   sortBy: " + sortBy);

            ArrayList<UserDisplay> result = adminDAO.searchAllUsersWithRole(keyword, roleId, activeStatus, sortBy);
            System.out.println("🔍 DEBUG AdminService.searchUsers() returned " + result.size() + " users");
            return result;
        } catch (SQLException e) {
            System.err.println("ADMIN Error searching users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int getSearchResultCount(String keyword, Integer roleId, Boolean activeStatus) {
        try {
            return adminDAO.countSearchResults(keyword, roleId, activeStatus);
        } catch (SQLException e) {
            System.err.println("ADMIN Error counting search results: " + e.getMessage());
            return 0;
        }
    }

    public ArrayList<Role> getAvailableRoles() {
        try {
            List<Role> roles = roleDao.findAll();
            return new ArrayList<>(roles);
        } catch (Exception e) {
            System.err.println("ADMIN Error getting roles: " + e.getMessage());
            ArrayList<Role> defaultRoles = new ArrayList<>();
            defaultRoles.add(createDefaultRole(1, "Admin"));
            defaultRoles.add(createDefaultRole(2, "Tech Manager"));
            defaultRoles.add(createDefaultRole(3, "Technician"));
            defaultRoles.add(createDefaultRole(4, "Store Keeper"));
            defaultRoles.add(createDefaultRole(5, "Accountant"));
            return defaultRoles;
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
            return adminDAO.countSearchResults(null, 1, null);
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

    // ===== HELPER METHODS =====
    private Role createDefaultRole(int roleId, String roleName) {
        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        return role;
    }
}