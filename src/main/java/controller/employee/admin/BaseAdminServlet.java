package controller.employee.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public abstract class BaseAdminServlet extends HttpServlet {

    protected String getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userName = (String) session.getAttribute("userName");
            if (userName != null && !userName.trim().isEmpty()) {
                return userName;
            }
        }
        return null;
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    protected Integer parseIntParameter(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Boolean parseStatusParameter(String param) {
        if (param == null || param.trim().isEmpty() || "all".equalsIgnoreCase(param)) {
            return null;
        }
        return "active".equalsIgnoreCase(param);
    }

    protected void handleMessages(HttpServletRequest request) {
        String message = request.getParameter("message");
        String messageType = request.getParameter("messageType");

        if (message != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType != null ? messageType : "info");
        }
    }

    protected boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    protected void redirectWithMessage(HttpServletResponse response, String url,
            String message, String messageType) throws IOException {
        // Đảm bảo URL chưa có dấu ?
        String separator = url.contains("?") ? "&" : "?";

        String redirectUrl = url + separator + "message=" + java.net.URLEncoder.encode(message, "UTF-8") +
                "&messageType=" + messageType;
        response.sendRedirect(redirectUrl);
    }
}