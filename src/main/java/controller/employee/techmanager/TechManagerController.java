package controller.employee.techmanager;

import model.employee.techmanager.TechManager;
import service.employee.TechManagerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet({ "/techmanager", "/techmanager/*" })
public class TechManagerController extends HttpServlet {
    private TechManagerService techManagerService;

    @Override
    public void init() throws ServletException {
        this.techManagerService = new TechManagerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/techmanager/home");
            return;
        }

        try {
            switch (pathInfo) {
                case "/home":
                    showDashboard(request, response);
                    break;
                case "/workorders":
                    request.getRequestDispatcher("/techmanager/workorders/list").forward(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get current user (Admin or TechManager)
        TechManager currentUser = getCurrentTechManager(request);
        System.out.println("TechManagerController DEBUG: showDashboard - currentUser: "
                + (currentUser != null ? currentUser.getUserName() + " (roleId=" + currentUser.getRoleId() + ")"
                : "null"));

        if (currentUser == null) {
            System.out.println("TechManagerController DEBUG: currentUser is null, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Use TechManagerService to get dashboard data
            TechManagerService.DashboardData dashboardData = techManagerService.getDashboardData(currentUser);

            request.setAttribute("currentUser", currentUser);
            request.setAttribute("workOrders", dashboardData.getWorkOrders());
            request.setAttribute("pendingCount", dashboardData.getPendingCount());
            request.setAttribute("inProcessCount", dashboardData.getInProcessCount());
            request.setAttribute("completedCount", dashboardData.getCompletedCount());
            request.setAttribute("isAdmin", dashboardData.isAdmin());

            request.getRequestDispatcher("/view/techmanager/home.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading dashboard", e);
        }
    }

    private TechManager getCurrentTechManager(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer userId = (Integer) session.getAttribute("userId");

            if (userId != null && userId > 0) {
                try {
                    // Use TechManagerService to get TechManager
                    TechManager techManager = techManagerService.getTechManagerByUserId(userId);
                    System.out.println("TechManagerController DEBUG: getTechManagerByUserId(" + userId + ") returned: "
                            + (techManager != null ? "success" : "null"));
                    return techManager;
                } catch (Exception e) {
                    System.out.println("TechManagerController DEBUG: error getting TechManager for userId=" + userId
                            + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("TechManagerController DEBUG: userId is null or <= 0: " + userId);
            }
        } else {
            System.out.println("TechManagerController DEBUG: session is null");
        }
        return null;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/view/error.jsp").forward(request, response);
    }
}