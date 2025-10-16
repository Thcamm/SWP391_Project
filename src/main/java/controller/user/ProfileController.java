package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import service.user.UserProfileService;
import service.user.UserProfileService.ValidationResult;

import java.io.IOException;
import java.sql.Date;

// Change the URL pattern here
@WebServlet(name = "ProfileController", urlPatterns = {"/user/profile"})
public class ProfileController extends HttpServlet {
    private UserProfileService userProfileService;

    @Override
    public void init() {
        this.userProfileService = new UserProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        if (sessionUser == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            showEditProfilePage(request, response, sessionUser.getUserId());
        } else {
            showViewProfilePage(request, response, sessionUser.getUserId());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        if (sessionUser == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        String birthDateStr = request.getParameter("birthDate");

        User updatedUser = new User();
        updatedUser.setUserId(sessionUser.getUserId());
        updatedUser.setFullName(fullName);
        updatedUser.setEmail(email);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setAddress(address);
        updatedUser.setGender(gender);
        try {
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                updatedUser.setBirthDate(Date.valueOf(birthDateStr));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        ValidationResult result = userProfileService.updateUserProfile(updatedUser);

        if (result.isValid()) {
            session.setAttribute("success", result.getMessage());
            session.setAttribute("user", userProfileService.getUserProfile(sessionUser.getUserId()));
            // Redirect to the new URL
            response.sendRedirect(contextPath + "/user/profile");
        } else {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("user", updatedUser);
            request.getRequestDispatcher("/view/user/editProfile.jsp").forward(request, response);
        }
    }

    private void showViewProfilePage(HttpServletRequest request, HttpServletResponse response, int userId) throws ServletException, IOException {
        User user = userProfileService.getUserProfile(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/view/user/viewProfile.jsp").forward(request, response);
    }

    private void showEditProfilePage(HttpServletRequest request, HttpServletResponse response, int userId) throws ServletException, IOException {
        User user = userProfileService.getUserProfile(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/view/user/editProfile.jsp").forward(request, response);
    }
}