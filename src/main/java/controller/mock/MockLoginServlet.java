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

//        req.getSession().setAttribute("userId",999);
//        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/rbac/roles"));


        User mockUser = new User();
        mockUser.setUserId(1001);
        session.setAttribute("user", mockUser);
        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}