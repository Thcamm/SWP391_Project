package controller.auth;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.user.User;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "GoogleCallback", urlPatterns = {"/auth/google/callback"})
public class GoogleCallback extends HttpServlet {

    private static final String CLIENT_ID = "188697502625-7r23admsb47s01746uo5iccn9lcao5pi.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:9999/GarageSystem_war/auth/google/callback";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    REDIRECT_URI)
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
