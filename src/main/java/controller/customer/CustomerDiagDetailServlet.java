package controller.customer;


import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.diagnostic.CustomerDiagnosticService;


import java.io.IOException;

@WebServlet("/customer/diagnostic/detail")
public class CustomerDiagDetailServlet extends BaseCustomerServlet {

    private final CustomerDiagnosticService service = new CustomerDiagnosticService();

    // GET /customer/diagnostic/detail?requestId=..&diagnosticId=..
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int requestId    = parseInt(req.getParameter("requestId"), 0);
        int diagnosticId = parseInt(req.getParameter("diagnosticId"), 0);

        if (requestId <= 0 || diagnosticId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "requestId/diagnosticId is required");
            return;
        }

        ServiceResult result = service.detail(requestId, diagnosticId);

        req.setAttribute("result", result);
        req.getRequestDispatcher("/view/customer/diagnostic/detail.jsp")
                .forward(req, resp);
    }
}
