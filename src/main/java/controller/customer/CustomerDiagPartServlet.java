package controller.customer;


import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.diagnostic.CustomerDiagnosticService;


import java.io.IOException;

@WebServlet("/customer/diagnostic/part")
public class CustomerDiagPartServlet extends BaseCustomerServlet {

    private final CustomerDiagnosticService service = new CustomerDiagnosticService();

    // POST /customer/diagnostic/part
    // params: diagnosticPartId, approved=true/false, diagnosticId?, requestId?
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int partId = parseInt(req.getParameter("diagnosticPartId"), 0);
        boolean approved = "true".equalsIgnoreCase(req.getParameter("approved"))
                || "1".equals(req.getParameter("approved"));

        if (partId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "diagnosticPartId is required");
            return;
        }

        ServiceResult result = service.updatePartApproval(partId, approved);

        // flash message
        req.getSession().setAttribute("flash", result);

        int diagnosticId = parseInt(req.getParameter("diagnosticId"), 0);
        int requestId    = parseInt(req.getParameter("requestId"), 0);

        if (diagnosticId > 0 && requestId > 0) {
            resp.sendRedirect(req.getContextPath()
                    + "/customer/diagnostic/detail?requestId=" + requestId
                    + "&diagnosticId=" + diagnosticId);
        } else {
            // fallback: quay về list
            if (requestId > 0) {
                resp.sendRedirect(req.getContextPath()
                        + "/customer/diagnostic/list?requestId=" + requestId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/");
            }
        }
    }
}

