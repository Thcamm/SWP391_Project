package controller.customer;

import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.vehicle.VehicleDiagnosticService;

import java.io.IOException;
import java.util.List;

@WebServlet("/customer/task-detail")
public class CustomerTaskDetailServlet extends HttpServlet {
    private final VehicleDiagnosticService diagnosticService = new VehicleDiagnosticService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String assignmentIdStr = req.getParameter("assignmentId");
        if (assignmentIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/customer/requests");
            return;
        }

        int assignmentId = Integer.parseInt(assignmentIdStr);
        ServiceResult rs = diagnosticService.getDiagnosticsByAssignmentId(assignmentId);

        if (rs.isError()) {
            req.setAttribute("error", rs.getMessage());
        } else {
            req.setAttribute("diagnostics", rs.getData(List.class));
        }

        req.getRequestDispatcher("/view/customer/task-detail.jsp").forward(req, resp);
    }
}