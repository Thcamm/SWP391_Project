package controller.auth;

import com.google.api.client.googleapis.auth.oauth2.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "GoogleLogin", urlPatterns = {"/auth/google"})
public class GoogleLogin extends HttpServlet {

    private static final String CLIENT_ID = "188697502625-7r23admsb47s01746uo5iccn9lcao5pi.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:9999/GarageSystem_war/auth/google/callback";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String authUrl = new GoogleAuthorizationCodeRequestUrl(
                CLIENT_ID,
                REDIRECT_URI,
                java.util.Arrays.asList(
                        "email",
                        "profile",
                        "openid"
                ))
                .setState("random_state_string")
                .build();

        response.sendRedirect(authUrl);
    }
}
