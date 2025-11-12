package controller.customer;

import common.constant.MessageConstants;
import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.vehicle.VehicleDiagnostic;
import service.diagnostic.CustomerDiagnosticService;

import java.io.IOException;

@WebServlet("/customer/diagnostic/view")
public class CustomerDiagnosticServlet extends HttpServlet {
    private final CustomerDiagnosticService customerDiagnosticService = new CustomerDiagnosticService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int diagnosticId = Integer.parseInt(req.getParameter("diagnosticId"));
        ServiceResult rs = customerDiagnosticService.getDiagnosticFullInfo(diagnosticId);

        if (rs.isError()) {
            req.getSession().setAttribute("errorMessage", rs.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customer/requests");
            return;
        }

        VehicleDiagnostic diagnostic = rs.getData(VehicleDiagnostic.class);
        req.setAttribute("diagnostic", diagnostic);
        req.getRequestDispatcher("/view/customer/diagnostic-detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int diagnosticId = Integer.parseInt(req.getParameter("diagnosticId"));
        String action = req.getParameter("action");

        ServiceResult rs;
        if ("approve".equals(action)) {
            rs = customerDiagnosticService.finalizeDiagnosticDecision(diagnosticId, true, null);
        } else if ("reject".equals(action)) {
            String reason = req.getParameter("reason");
            rs = customerDiagnosticService.finalizeDiagnosticDecision(diagnosticId, false, reason);
        } else {
            rs = ServiceResult.error(MessageConstants.ERR006);
        }

        req.getSession().setAttribute(rs.isError() ? "errorMessage" : "successMessage", rs.getMessage());
        resp.sendRedirect(req.getContextPath() + "/customer/diagnostic/view?diagnosticId=" + diagnosticId);
    }
}