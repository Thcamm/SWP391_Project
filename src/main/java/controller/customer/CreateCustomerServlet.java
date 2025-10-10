package controller.customer;

import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.Customer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@WebServlet("/create-customer")
public class CreateCustomerServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String birthDateRaw = request.getParameter("birthDate");
        LocalDate birthDate = null;

        if (birthDateRaw != null && !birthDateRaw.isEmpty()) {
            birthDate = LocalDate.parse(birthDateRaw);
        }

        Customer customer = new Customer(fullName, email, phoneNumber, gender, address, birthDate);
        CustomerDAO dao = new CustomerDAO();

        boolean isDuplicate = dao.isCustomerDuplicate(email, phoneNumber);
        if (isDuplicate) {
            request.setAttribute("message", "⚠️ Email hoặc số điện thoại đã tồn tại!");
            request.setAttribute("messageType", "warning");
            request.getRequestDispatcher("/create-customer.jsp").forward(request, response);
            return;
        }
        boolean success = dao.insertCustomer(customer);
        if (success) {
            request.setAttribute("message", "✅ Thêm khách hàng thành công!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "❌ Không thể thêm khách hàng. Vui lòng thử lại.");
            request.setAttribute("messageType", "warning");
        }
        request.getRequestDispatcher("/create-customer-result.jsp").forward(request, response);


    }
}
