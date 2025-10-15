package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

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
        // Set encoding để xử lý tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Lấy thông tin từ form (theo thứ tự steps)
        // Step 1: Personal Information
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");

        // Step 2: Information
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String dateOfBirth = request.getParameter("birthDate");
        java.sql.Date sqlDateOfBirth = null;
        LocalDate localDate = LocalDate.parse(dateOfBirth);
        sqlDateOfBirth = java.sql.Date.valueOf(localDate);
        // Step 3: Account Information
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String iAgree = request.getParameter("iAgree");

        // Kiểm tra thông tin bắt buộc
        if (firstName == null || lastName == null || phoneNumber == null || email == null || gender == null || gender.trim().isEmpty() || dateOfBirth == null || dateOfBirth.trim().isEmpty() ||
                address == null || userName == null || password == null ||
                firstName.trim().isEmpty() || lastName.trim().isEmpty() ||
                phoneNumber.trim().isEmpty() || email.trim().isEmpty() ||
                address.trim().isEmpty() || userName.trim().isEmpty() ||
                password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin bắt buộc.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }


        // Hash password
        String hashedPassword = util.PasswordUtil.hashPassword(password);

        // Tạo fullName từ firstName và lastName
        String fullName = firstName.trim() + " " + lastName.trim();

        // Tạo user mới
        model.user.User user = new model.user.User();
        user.setFullName(fullName);
        user.setUserName(userName.trim());
        user.setEmail(email.trim());
        user.setPhoneNumber(phoneNumber.trim());
        user.setPasswordHash(hashedPassword);
        user.setGender(gender.trim());
        user.setBirthDate(sqlDateOfBirth);
        user.setRoleId(5); // Role mặc định là Customer
        user.setActiveStatus(true);
        user.setAddress(address.trim());

        // Lưu user vào database
        try {
            dao.user.UserDAO userDAO = new dao.user.UserDAO();

            // Kiểm tra username đã tồn tại
            if (userDAO.getUserByUserName(userName) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Kiểm tra email đã tồn tại (nếu có method)
            // if (userDAO.getUserByEmail(email) != null) {
            //     request.setAttribute("error", "Email đã được sử dụng.");
            //     request.getRequestDispatcher("/register.jsp").forward(request, response);
            //     return;
            // }

            boolean success = userDAO.addUser(user);

            if (success) {
                // Chuyển hướng về trang login với thông báo thành công
                response.sendRedirect("login.jsp?success=true");
            } else {
                request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}