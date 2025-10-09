package controller.auth;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.user.User;
import util.GoogleConfig;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "GoogleCallback", urlPatterns = {"/auth/google/callback"})
public class GoogleCallback extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    GoogleConfig.getClientId(),
                    GoogleConfig.getClientSecret(),
                    code,
                    GoogleConfig.getRedirectUri())
                    .execute();

            GoogleIdToken idToken = tokenResponse.parseIdToken();
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);

            if (user == null) {
                // Tạo user mới từ Google
                user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setUserName(email);
                user.setRoleId(2); // Role mặc định
                user.setActiveStatus(true);
                userDAO.insertGoogleUser(user);
                user = userDAO.getUserByEmail(email);
            }

            request.getSession().setAttribute("user", user);
            request.getSession().setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/Home");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=google_login_failed");
        }
    }
}