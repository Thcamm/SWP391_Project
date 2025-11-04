package controller.employee.accountant;

import dao.invoice.InvoiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.invoice.Invoice; // Thêm Model Hóa đơn
import model.user.User;

import java.io.IOException;

@WebServlet(name = "AccountantDashboardServlet", urlPatterns = {"/accountant/home"})
public class AccountantDashboardServlet extends HttpServlet { // 2. Đổi tên class

    private InvoiceDAO invoiceDAO;

    @Override
    public void init() throws ServletException {
        invoiceDAO = new InvoiceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        String roleCode = (String) session.getAttribute("roleCode"); // Lấy roleCode

        if (currentUser == null || !"ACCOUNTANT".equals(roleCode)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/view/accountant/home.jsp").forward(request, response);
    }
}
