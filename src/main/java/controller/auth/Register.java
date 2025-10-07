package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy thông tin từ form
        String fullName = request.getParameter("fullName");
        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");

        // Kiểm tra thông tin bắt buộc (trừ password)
        if (fullName == null || userName == null || email == null || phoneNumber == null ||
                fullName.isEmpty() || userName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra password riêng cho đăng ký thông thường
        if (password == null || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Hash password
        String hashedPassword = util.PasswordUtil.hashPassword(password);

        // Tạo user mới
        model.user.User user = new model.user.User();
        user.setFullName(fullName);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(hashedPassword);
        user.setRoleId(2);
        user.setActiveStatus(true);

        // Lưu user vào database
        boolean success = false;
        try {
            dao.user.UserDAO userDAO = new dao.user.UserDAO();
            if (userDAO.getUserByUserName(userName) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            success = userDAO.addUser(user);
        } catch (Exception e) {
            request.setAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (success) {
            response.sendRedirect("login.jsp");
        } else {
            request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}