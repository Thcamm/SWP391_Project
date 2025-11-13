package controller.customer;

import dao.customer.CustomerDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.user.User;

import java.sql.SQLException;

public abstract class BaseCustomerServlet extends HttpServlet {

    protected final CustomerDAO customerDAO = new CustomerDAO();

    protected Integer getLoggedCustomerId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        // 1. Lấy user từ session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null; // chưa login
        }

        // 2. Nếu đã cache customerId trong session thì dùng luôn
        Object cached = session.getAttribute("customerId");
        if (cached instanceof Integer) {
            return (Integer) cached;
        }

        // 3. Chưa có thì gọi DAO lấy từ DB
        Integer customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId != null) {
            session.setAttribute("customerId", customerId); // cache lại
        }
        return customerId;
    }

    protected int parseInt(String s, int defaultVal) {
        if (s == null || s.isBlank()) return defaultVal;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
