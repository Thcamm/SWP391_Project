package controller.auth;

import com.google.api.client.googleapis.auth.oauth2.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.GoogleConfig;

import java.io.IOException;

@WebServlet(name = "GoogleLogin", urlPatterns = {"/auth/google"})
public class GoogleLogin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String clientId = util.GoogleConfig.getClientId();
        String redirectUri = util.GoogleConfig.getRedirectUri();

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=email%20profile"
                + "&access_type=offline";

        response.sendRedirect(authUrl);
    }
}
