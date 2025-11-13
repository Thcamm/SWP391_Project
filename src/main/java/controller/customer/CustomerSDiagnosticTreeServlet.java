package controller.customer;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.CustomerDiagnosticsView;
import service.diagnostic.CustomerDiagnosticService;


import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customer/diagnostic/tree")
public class CustomerSDiagnosticTreeServlet extends BaseCustomerServlet {

    private final CustomerDiagnosticService service = new CustomerDiagnosticService();

    // GET /customer/diagnostic/tree?requestId=...
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int requestId = parseInt(req.getParameter("requestId"), 0);
        if (requestId <= 0) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "requestId is required");
            return;
        }

        Integer customerId = getLoggedCustomerId(req);
        if (customerId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            CustomerDiagnosticsView view =
                    service.getDiagnosticsForRequest(customerId, requestId);

            System.out.println("loi view");
            if (view == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("view", view);
            System.out.println("loi redirect");
            req.getRequestDispatcher("/view/customer/diagnostic/tree.jsp")
                    .forward(req, resp);

        } catch (SecurityException ex) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền xem request này");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

