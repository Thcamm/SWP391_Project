package controller.customer;

import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.diagnostic.CustomerDiagnosticService;


import java.io.IOException;

@WebServlet("/customer/diagnostic/finalize")
public class CustomerDiagFinalizeServlet extends BaseCustomerServlet {

    private final CustomerDiagnosticService service = new CustomerDiagnosticService();

    // POST /customer/diagnostic/finalize
    // params: diagnosticId, approve=true/false, rejectReason?, requestId?
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int diagnosticId = parseInt(req.getParameter("diagnosticId"), 0);
        boolean approve = "true".equalsIgnoreCase(req.getParameter("approve"))
                || "1".equals(req.getParameter("approve"));
        String rejectReason = req.getParameter("rejectReason");

        if (diagnosticId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "diagnosticId is required");
            return;
        }

        ServiceResult result =
                service.finalizeDiagnosticDecision(diagnosticId, approve, rejectReason);

        int requestId = parseInt(req.getParameter("requestId"), 0);

        req.getSession().setAttribute("flash", result);

        if (requestId > 0) {
            resp.sendRedirect(req.getContextPath()
                    + "/customer/diagnostic/list?requestId=" + requestId);
        } else {
            resp.sendRedirect(req.getContextPath()
                    + "/customer/diagnostic/detail?diagnosticId=" + diagnosticId);
        }
    }
}
