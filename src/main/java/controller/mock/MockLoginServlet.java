package controller.mock;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;

import java.io.IOException;

@WebServlet(urlPatterns = "/test/login")
public class MockLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        req.getSession().setAttribute("userId", 999);
        resp.getWriter().println("Mock login success. <a href='" + req.getContextPath() + "/rbac/roles'>Go to Roles</a>");

        // 1. Tạo một đối tượng User giả lập

    }
}