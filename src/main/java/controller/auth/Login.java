package controller.auth;

import dao.customer.CustomerDAO;
import dao.employee.admin.rbac.RoleDao;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.user.User;
import service.auth.AuthService;
import service.user.UserLoginService;
import util.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

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

            Set<String> userPermissions = null;
            try {
                userPermissions = new AuthService(new RoleDao()).getPermissionCodesOfUser(user.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            request.getSession().setAttribute("userPermissions", userPermissions);


            request.getSession().setMaxInactiveInterval(30 * 60);
            String redirectAfterLogin = (String) request.getSession().getAttribute("redirectAfterLogin");
            if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                request.getSession().removeAttribute("redirectAfterLogin");
                response.sendRedirect(redirectAfterLogin);
                return;
            }
//7 roles: ADMIN, TechManager, Technical, Accountant, Store Keeper, Customer Service,Customer
            if(user.getRoleId() == 1) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            } else if(user.getRoleId() == 2) {
                response.sendRedirect(request.getContextPath() + "/techmanager/home");
                return;
            } else if(user.getRoleId() == 3) {
                response.sendRedirect(request.getContextPath() + "/technician/home");
                return;
            } else if(user.getRoleId() == 4) {
                response.sendRedirect(request.getContextPath() + "/inventory/dashboard");
                return;
            } else if(user.getRoleId() == 5) {
                response.sendRedirect(request.getContextPath() + "/accountant/home");
                return;
            } else if(user.getRoleId() == 6) {
                response.sendRedirect(request.getContextPath() + "/customerservice/home");
                return;
            } else if(user.getRoleId() == 7) {
                response.sendRedirect(request.getContextPath() + "/Home");
                return;
            }        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}