package controller.employee.accountant;

import dao.invoice.InvoiceDAO; // Thêm DAO Hóa đơn
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.invoice.Invoice; // Thêm Model Hóa đơn
import model.user.User;

import java.io.IOException;
import java.sql.SQLException; // Thêm SQLException
import java.util.List;          // Thêm List

// 1. Đổi URL pattern cho khớp với LoginServlet
@WebServlet(name = "AccountantDashboardServlet", urlPatterns = {"/accountant/dashboard"})
public class AccountantDashboardServlet extends HttpServlet { // 2. Đổi tên class

    private InvoiceDAO invoiceDAO; // 3. Thêm InvoiceDAO

    @Override
    public void init() throws ServletException {
        invoiceDAO = new InvoiceDAO(); // 4. Khởi tạo DAO
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        String roleCode = (String) session.getAttribute("roleCode"); // Lấy roleCode

        // 5. Kiểm tra Role Code thay vì Role ID
        if (currentUser == null || !"ACCOUNTANT".equals(roleCode)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // 6. LẤY DỮ LIỆU THẬT THAY VÌ MOCK
            // Lấy danh sách hóa đơn chưa thanh toán hoặc thanh toán một phần
            List<Invoice> unpaidInvoices = invoiceDAO.getInvoicesByStatus("UNPAID", "PARTIALLY_PAID");

            // Lấy tổng doanh thu (ví dụ: tổng các hóa đơn đã thanh toán)
            // double totalRevenue = invoiceDAO.getTotalRevenue(); // Cần thêm hàm này vào DAO

            // Đặt dữ liệu vào request
            request.setAttribute("unpaidInvoices", unpaidInvoices);
            request.setAttribute("unpaidCount", unpaidInvoices.size());
            // request.setAttribute("totalRevenue", totalRevenue);

            // Có thể thêm các số liệu khác nếu cần (overdue, paidToday...)

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load dashboard data.");
        }

        // 7. Forward đến đúng file JSP dashboard
        request.getRequestDispatcher(request.getContextPath()+ "/view/accountant/dashboard.jsp").forward(request, response);
    }
}
