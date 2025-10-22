package controller.employee.accountant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;

import java.io.IOException;

// Update the URL pattern to match the desired endpoint
@WebServlet(name = "AccountantHomeServlet", urlPatterns = {"/accountant/home"})
public class AccountantHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Check if user is logged in and has the correct role
        if (currentUser == null || currentUser.getRoleId() != 5) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Mock data for the view (will be replaced with real data later)
        request.setAttribute("totalRevenue", 125500000);
        request.setAttribute("unpaidCount", 12);
        request.setAttribute("overdueCount", 3);
        request.setAttribute("paidToday", 5);

        // Forward to the JSP page
        request.getRequestDispatcher("/view/accountant/home.jsp").forward(request, response);
    }
}
