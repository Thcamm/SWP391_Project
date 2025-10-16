package controller.auth;

import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.User;
import util.PasswordUtil;
import service.user.UserLoginService;

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

            // Mật khẩu đúng!
            request.getSession().setAttribute("user", user);
            request.getSession().setAttribute("userName", user.getUserName());
            request.getSession().setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/Home");

        } else {
            errorMessage = "Invalid username or password.";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}