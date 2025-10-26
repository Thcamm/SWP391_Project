package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserChooseTypeServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns /UserChooseTypeServlet and /UserChooseTypeServlet/*.
 */
@WebServlet("/admin/users/choose-type")
public class UserChooseTypeServlet extends BaseAdminServlet {
    /**
     * Handles HTTP GET requests.
     * Typically used to retrieve data or display a user interface.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("currentUser", getCurrentUser(request));
        handleMessages(request); // Xử lý nếu có redirect lỗi từ đâu đó

        System.out.println("UserController - Displaying choose type form for user: " + getCurrentUser(request));

        request.getRequestDispatcher("/view/admin/choose-user-type.jsp")
                .forward(request, response);
    }

    /**
     * Handles HTTP POST requests.
     * Typically used to receive form data and execute business logic (e.g., saving to a database).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Không nên có POST request đến URL này
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method is not supported.");
    }
}
