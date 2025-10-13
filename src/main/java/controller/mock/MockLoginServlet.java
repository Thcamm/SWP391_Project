package controller.mock;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;

import java.io.IOException;

@WebServlet(urlPatterns = "/mock/login")
public class MockLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        req.getSession().setAttribute("userId",999);
        req.getSession().setAttribute("roleCode", "ADMIN");
        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/admin/rbac/roles"));


    }
}