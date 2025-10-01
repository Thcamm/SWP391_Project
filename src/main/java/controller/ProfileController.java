package controller;

import model.User;
import service.UserProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/me")
public class ProfileController extends HttpServlet {
    private final UserProfileService profileService = new UserProfileService();

    // Use a fixed ID for testing until a login module is implemented.
    private static final int TEST_USER_ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Handle Flash Messages (Moved from JSP)
        HttpSession session = request.getSession(false);
        if (session != null) {

            // Move success message from session to request scope for one-time display
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }

            // Move error message from session to request scope
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
        }

        // 2. Fetch User Profile
        Optional<User> user = profileService.getUserProfile(TEST_USER_ID);

        // 3. Set data for the View
        if (user.isPresent()) {
            // If user is found, attach the User object to the request.
            request.setAttribute("user", user.get());
        } else {
            // Handle case where user ID is valid but profile is not found (e.g., DB error).
            request.setAttribute("errorMessage", "Không thể tải thông tin hồ sơ.");
        }

        // Forward to the JSP view (View handles displaying the form or the error message).
        request.getRequestDispatcher("/view/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set character encoding for Vietnamese inputs
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");

        boolean success = profileService.updateProfile(TEST_USER_ID, fullName, email, phoneNumber);

        if (success) {
            // Set success flash message
            request.getSession().setAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } else {
            // Set error flash message
            request.getSession().setAttribute("errorMessage", "Cập nhật hồ sơ thất bại. Vui lòng kiểm tra lại thông tin.");
        }

        // Redirect to GET /me to show updated data and clear flash messages (Post-Redirect-Get pattern)
        response.sendRedirect(request.getContextPath() + "/me");
    }
}