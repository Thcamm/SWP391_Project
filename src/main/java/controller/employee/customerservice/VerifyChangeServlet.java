package controller.employee.customerservice;

import dao.customer.PendingChangeDAO;
import dao.customer.CustomerDAO;
import model.customer.PendingChange;
import model.customer.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(urlPatterns = "/verify-change")
public class VerifyChangeServlet extends HttpServlet {

    private PendingChangeDAO pendingChangeDAO = new PendingChangeDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

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

            // Kiểm tra token hết hạn
            if (pending.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
                pendingChangeDAO.updateStatus(pending.getChangeId(), "REJECTED");
                response.getWriter().println("Token đã hết hạn. Yêu cầu thay đổi không được áp dụng.");
                return;
            }

            // Áp dụng các thay đổi vào Customer
            Customer customer = customerDAO.getCustomerById(pending.getCustomerId());
            Map<String, String> fields = pending.getFieldsChanged();

            if (fields.containsKey("FullName")) customer.setFullName(fields.get("FullName"));
            if (fields.containsKey("Email")) customer.setEmail(fields.get("Email"));
            if (fields.containsKey("PhoneNumber")) customer.setPhoneNumber(fields.get("PhoneNumber"));
            if (fields.containsKey("Gender")) customer.setGender(fields.get("Gender"));
            if (fields.containsKey("Address")) customer.setAddress(fields.get("Address"));
            if (fields.containsKey("NewPassword")) customer.setPasswordHash(fields.get("NewPassword")); // Lưu hashed ở DAO

            customerDAO.updateCustomer(customer);

            // Cập nhật trạng thái pending
            pendingChangeDAO.updateStatus(pending.getChangeId(), "CONFIRMED");

            // Không cần hiển thị gì, chỉ bấm là xong
            response.getWriter().println("Cập nhật thông tin thành công.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

