package controller.customer;

import common.message.ServiceResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.diagnostic.CustomerDiagnosticService;


import java.io.IOException;

@WebServlet("/customer/diagnostic/list")
public class CustomerDiagListServlet extends BaseCustomerServlet {

    private final CustomerDiagnosticService service = new CustomerDiagnosticService();

    // GET /customer/diagnostic/list?requestId=...
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int requestId = parseInt(req.getParameter("requestId"), 0);
        if (requestId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "requestId is required");
            return;
        }

        ServiceResult result = service.listByRequest(requestId);

        req.setAttribute("result", result);
        req.getRequestDispatcher("/view/customer/diagnostic/list.jsp")
                .forward(req, resp);
    }
}
