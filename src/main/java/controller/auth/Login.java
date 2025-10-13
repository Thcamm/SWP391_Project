package controller.auth;

import dao.customer.CustomerDAO;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.user.User;
import util.PasswordUtil;
import service.user.UserLoginService;

import java.io.IOException;
import java.sql.SQLException;

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

        UserDAO userDAO = new UserDAO();
        UserLoginService userService = new UserLoginService(userDAO);
        User user = userService.findByUserName(username);

        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {

            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = null;
            try {
                customer = customerDAO.getCustomerByUserId(user.getUserId());
            } catch (SQLException e) {
                e.printStackTrace();
            }


            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            if (customer != null) {
                session.setAttribute("customer", customer);
            }

            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/Home");

        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}