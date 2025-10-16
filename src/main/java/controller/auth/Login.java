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
import service.user.UserLoginService;
import util.PasswordUtil;

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
        String errorMessage = null;

        UserDAO userDAO = new UserDAO();
        UserLoginService userService = new UserLoginService(userDAO);
        User user = userService.findByUserName(username);



        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {

            String roleCode = new dao.employee.admin.rbac.RoleDao().findRoleCodeById(user.getRoleId());
            request.getSession().setAttribute("user", user);
            request.getSession().setAttribute("roleCode", roleCode);
            request.getSession().setAttribute("userId", user.getUserId());
            request.getSession().setAttribute("userName", user.getUserName());

            CustomerDAO customerDAO = new CustomerDAO();
            try {
                Customer customer = customerDAO.getCustomerByUserId(user.getUserId());
                if (customer != null) {
                    request.getSession().setAttribute("customer", customer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            request.getSession().setMaxInactiveInterval(30 * 60);
            String redirectAfterLogin = (String) request.getSession().getAttribute("redirectAfterLogin");
            if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                request.getSession().removeAttribute("redirectAfterLogin");
                response.sendRedirect(redirectAfterLogin);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/Home");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}