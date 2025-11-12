package controller.customer;

import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.diagnostic.CustomerDiagnosticService;

import java.io.IOException;

@WebServlet("/customer/diagnostic/part-approval")
public class CustomerPartApprovalServlet extends HttpServlet {
        private final CustomerDiagnosticService customerService = new CustomerDiagnosticService();

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

            int partId = Integer.parseInt(req.getParameter("partId"));
            int diagnosticId = Integer.parseInt(req.getParameter("diagnosticId"));
            boolean approved = Boolean.parseBoolean(req.getParameter("approved"));

            ServiceResult rs = customerService.updatePartApproval(partId, approved);
            req.getSession().setAttribute(rs.isError() ? "errorMessage" : "successMessage", rs.getMessage());

            resp.sendRedirect(req.getContextPath() + "/customer/diagnostic/view?diagnosticId=" + diagnosticId);
        }

}
