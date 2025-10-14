package controller.auth;

import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import service.user.UserLoginService;
import util.PasswordUtil;

import java.io.IOException;

@WebServlet("/login")
public class Login extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String errorMessage = null;

        UserDAO userDAO = new UserDAO();
        UserLoginService userService = new UserLoginService(userDAO);
        User user = userService.findByUserName(username);

        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userName", user.getUserName());
            session.setMaxInactiveInterval(30 * 60);

            // ✅ Kiểm tra xem có URL nào lưu trước đó không
            String redirectAfterLogin = (String) session.getAttribute("redirectAfterLogin");
            if (redirectAfterLogin != null) {
                session.removeAttribute("redirectAfterLogin"); // dọn dẹp
                response.sendRedirect(redirectAfterLogin);
            } else {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
            }
        } else {
            errorMessage = "Invalid username or password.";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}