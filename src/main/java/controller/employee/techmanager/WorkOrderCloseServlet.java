package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.employee.techmanager.WorkOrderCloseDTO;
import service.employee.techmanager.WorkOrderCloseService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/techmanager/close-workorders")
public class WorkOrderCloseServlet extends HttpServlet {

    private WorkOrderCloseService service;

    @Override
    public void init() {
        this.service = new WorkOrderCloseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");

        if (userName == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Integer techManagerId = service.getTechManagerEmployeeId(userName);
            if (techManagerId == null) {
                request.setAttribute("errorMessage", "TechManager not found");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }

            List<WorkOrderCloseDTO> workOrders = service.getAllInProgressWorkOrders(techManagerId);
            int readyCount = (int) workOrders.stream().filter(wo -> wo.getActiveTasks() == 0).count();

            request.setAttribute("workOrders", workOrders);
            request.setAttribute("totalWorkOrders", workOrders.size());
            request.setAttribute("totalReady", readyCount);

            request.getRequestDispatcher("/view/techmanager/close-workorders.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");

        if (userName == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String workOrderIdStr = request.getParameter("workOrderID");

        if (!"close".equals(action) || workOrderIdStr == null) {
            redirectWithMessage(request, response, "Invalid parameters", "error");
            return;
        }

        try {
            int workOrderId = Integer.parseInt(workOrderIdStr);
            Integer techManagerId = service.getTechManagerEmployeeId(userName);

            if (techManagerId == null) {
                redirectWithMessage(request, response, "TechManager not found", "error");
                return;
            }

            String message = service.closeWorkOrder(workOrderId, techManagerId);
            redirectWithMessage(request, response, message, "success");

        } catch (IllegalStateException e) {
            redirectWithMessage(request, response, e.getMessage(), "warning");
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(request, response, "Error: " + e.getMessage(), "error");
        }
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
            String message, String type) throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() +
                "/techmanager/close-workorders?message=" + encoded + "&type=" + type);
    }
}
