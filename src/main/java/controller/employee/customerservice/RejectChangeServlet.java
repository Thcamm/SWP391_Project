package controller.employee.customerservice;


import dao.customer.PendingChangeDAO;
import model.customer.PendingChange;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/reject-change")
public class RejectChangeServlet extends HttpServlet {

    private PendingChangeDAO pendingChangeDAO = new PendingChangeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token không hợp lệ!");
            return;
        }

        try {
            PendingChange pending = pendingChangeDAO.getByToken(token);
            if (pending == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy yêu cầu thay đổi.");
                return;
            }

            pendingChangeDAO.updateStatus(pending.getChangeId(), "REJECTED");

            // Không cần hiển thị gì
            response.getWriter().println("Yêu cầu thay đổi đã bị từ chối.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

