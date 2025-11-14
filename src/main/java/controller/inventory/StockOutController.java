package controller.inventory;

import dao.inventory.WorkOrderPartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.inventory.WorkOrderPart;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "WorkOrderPartController", urlPatterns = {"/stock-out"})
public class StockOutController extends HttpServlet {

    private WorkOrderPartDAO workOrderPartDAO = new WorkOrderPartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                listPendingRequests(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/stock-out?action=list");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action.equals("approve")) {
                approveRequest(request, response);
            } else if (action.equals("reject")) {
                rejectRequest(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    /**
     * Display pending requests
     */
    private void listPendingRequests(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<WorkOrderPart> pendingRequests = workOrderPartDAO.getPendingRequests();
        request.setAttribute("pendingRequests", pendingRequests);
        request.getRequestDispatcher("/view/storekeeper/stock-out.jsp").forward(request, response);
    }

    /**
     * Approve request and deduct stock
     */
    private void approveRequest(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int workOrderPartId = Integer.parseInt(request.getParameter("workOrderPartId"));

        boolean success = workOrderPartDAO.approveRequest(workOrderPartId);

        if (success) {
            response.sendRedirect(request.getContextPath() +
                    "/stock-out?action=list&message=approved");
        } else {
            response.sendRedirect(request.getContextPath() +
                    "/stock-out?action=list&error=insufficient_stock");
        }
    }

    /**
     * Reject request manually
     */
    private void rejectRequest(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int workOrderPartId = Integer.parseInt(request.getParameter("workOrderPartId"));
        String reason = request.getParameter("reason");

        boolean success = workOrderPartDAO.rejectRequest(workOrderPartId, reason);

        if (success) {
            response.sendRedirect(request.getContextPath() +
                    "/workorderpart?action=list&message=rejected");
        } else {
            response.sendRedirect(request.getContextPath() +
                    "/workorderpart?action=list&error=reject_failed");
        }
    }
}
