package controller.user;

import dao.user.UserDAO;
import model.user.User;
import util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/changePassword"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Simply display the change password form
        request.getRequestDispatcher("/view/user/changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match.");
            request.getRequestDispatcher("/view/user/changePassword.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User currentUser = userDAO.getUserById(user.getUserId());
            if (currentUser == null) {
                response.sendRedirect("login");
                return;
            }

            if (!PasswordUtil.checkPassword(oldPassword, currentUser.getPasswordHash())) {
                request.setAttribute("error", "Incorrect current password.");
                request.getRequestDispatcher("/view/user/changePassword.jsp").forward(request, response);
                return;
            }

            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            boolean success = userDAO.updatePassword(user.getUserId(), newPasswordHash);

            if (success) {
                request.setAttribute("success", "Password changed successfully!");
            } else {
                request.setAttribute("error", "An error occurred. Please try again.");
            }
            request.getRequestDispatcher("/view/user/changePassword.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred.");
            request.getRequestDispatcher("/view/user/changePassword.jsp").forward(request, response);
        }
    }
}