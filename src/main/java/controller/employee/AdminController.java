package controller.employee;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import model.user.User;
import service.employee.AdminService;

@WebServlet("/admin/users/*")
public class AdminController extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
        System.out.println("AdminController initialized at " + java.time.LocalDateTime.now());
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String currentUser = "JuroMark";

        // Kiểm tra quyền admin đơn giản
        if (!adminService.isAdmin(currentUser)) {
            request.setAttribute("errorMessage", "Bạn không có quyền truy cập trang này!");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // Lấy parameters tìm kiếm
        String keyword = request.getParameter("keyword");
        String roleParam = request.getParameter("role");
        String message = request.getParameter("message");
        String messageType = request.getParameter("messageType");
        List<User> users;

        // Xử lý tìm kiếm đơn giản
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = adminService.searchUsers(keyword);
            request.setAttribute("searchKeyword", keyword);
            System.out.println("Tìm kiếm với từ khóa: " + keyword);
        } else if (roleParam != null && !roleParam.trim().isEmpty()) {
            try {
                int roleId = Integer.parseInt(roleParam);
                users = adminService.getUsersByRole(roleId);
                request.setAttribute("selectedRole", roleId);
                System.out.println("Lọc theo role: " + roleId);
            } catch (NumberFormatException e) {
                users = adminService.getAllUsers();
            }
        } else {
            users = adminService.getAllUsers();
        }

        int totalUsers = adminService.countUsers();

        //send data to JSP
        request.setAttribute("users", users);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("currentUser", currentUser);

        // send message if exists
        if (message != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
        }

        System.out.println("User: " + currentUser + " - Hiển thị " + users.size() + " users");

        // redirect to JSP
        request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp")
                .forward(request, response);

    }

    /**
     * Handles HTTP POST requests.
     * Typically used to receive form data and execute business logic (e.g., saving to a database).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setContentType("text/plain;charset=UTF-8");

    }
        private String handleChangeRole (HttpServletRequest request, String currentUser){
            String userIdParam = request.getParameter("userId");
            String newRoleParam = request.getParameter("newRole");

            System.out.println("Thay đổi role - UserId: " + userIdParam + ", NewRole: " + newRoleParam);

            try {
                int userId = Integer.parseInt(userIdParam);
                int newRoleId = Integer.parseInt(newRoleParam);

                if (userId <= 0 || newRoleId <= 0 || newRoleId > 5) {
                    return "Tham số không hợp lệ!";
                }

                boolean success = adminService.changeUserRole(userId, newRoleId, currentUser);

                if (success) {
                    System.out.println("Thay đổi role thành công cho user ID: " + userId);
                    return "Đã thay đổi role thành công!";
                } else {
                    System.out.println("Thay đổi role thất bại cho user ID: " + userId);
                    return "Thay đổi role thất bại!";
                }

            } catch (NumberFormatException e) {
                System.out.println("Lỗi parse số: " + e.getMessage());
                return "Tham số không hợp lệ!";
            }
        }

        private String handleToggleStatus (HttpServletRequest request, String currentUser){
            String userIdParam = request.getParameter("userId");

            System.out.println("Toggle status - UserId: " + userIdParam);

            try {
                int userId = Integer.parseInt(userIdParam);

                if (userId <= 0) {
                    return "User ID không hợp lệ!";
                }

                boolean success = adminService.toggleUserStatus(userId, currentUser);

                if (success) {
                    System.out.println("Toggle status thành công cho user ID: " + userId);
                    return "Đã thay đổi trạng thái thành công!";
                } else {
                    System.out.println("Toggle status thất bại cho user ID: " + userId);
                    return "Thay đổi trạng thái thất bại!";
                }

            } catch (NumberFormatException e) {
                return "User ID không hợp lệ!";
            }
        }
        private String handleResetPassword (HttpServletRequest request, String currentUser){
            String userIdParam = request.getParameter("userId");

            System.out.println("Reset password - UserId: " + userIdParam);

            try {
                int userId = Integer.parseInt(userIdParam);

                if (userId <= 0) {
                    return "User ID không hợp lệ!";
                }

                String newPassword = adminService.resetPassword(userId, currentUser);

                if (newPassword != null) {
                    System.out.println("Reset password thành công cho user ID: " + userId);
                    return "Reset password thành công! Mật khẩu mới: " + newPassword;
                } else {
                    System.out.println("Reset password thất bại cho user ID: " + userId);
                    return "Reset password thất bại!";
                }

            } catch (NumberFormatException e) {
                return "User ID không hợp lệ!";
            }
        }

        private String handleActivate (HttpServletRequest request, String currentUser){
            String userIdParam = request.getParameter("userId");

            System.out.println("Activate user - UserId: " + userIdParam);

            try {
                int userId = Integer.parseInt(userIdParam);

                if (userId <= 0) {
                    return "User ID không hợp lệ!";
                }

                boolean success = adminService.activateUser(userId, currentUser);

                if (success) {
                    System.out.println("Activate thành công cho user ID: " + userId);
                    return "Đã kích hoạt user thành công!";
                } else {
                    System.out.println("Activate thất bại cho user ID: " + userId);
                    return "Kích hoạt user thất bại!";
                }

            } catch (NumberFormatException e) {
                return "User ID không hợp lệ!";
            }
        }

        private String handleCreateUser (HttpServletRequest request, String currentUser){
            String fullName = request.getParameter("fullName");
            String userName = request.getParameter("userName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String roleParam = request.getParameter("role");

            System.out.println("Create user - Username: " + userName + ", Role: " + roleParam);

            try {
                // check required fields
                if (fullName == null || fullName.trim().isEmpty() ||
                        userName == null || userName.trim().isEmpty() ||
                        email == null || email.trim().isEmpty() ||
                        roleParam == null || roleParam.trim().isEmpty()) {

                    return "Vui lòng điền đầy đủ thông tin bắt buộc!";
                }

                int roleId = Integer.parseInt(roleParam);

                if (roleId <= 0 || roleId > 5) {
                    return "Role ID không hợp lệ!";
                }

                boolean success = adminService.createUser(fullName.trim(), userName.trim(),
                        email.trim(), roleId, currentUser);

                if (success) {
                    System.out.println("Tạo user thành công - Username: " + userName);
                    return "Đã tạo user mới thành công! Username: " + userName + ", Mật khẩu: 123456";
                } else {
                    System.out.println("Tạo user thất bại - Username: " + userName);
                    return "Tạo user thất bại! Username có thể đã tồn tại.";
                }

            } catch (NumberFormatException e) {
                return "Role ID không hợp lệ!";
            }
        }
    }