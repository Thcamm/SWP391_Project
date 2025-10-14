package controller.employee.customer_service;

import common.utils.RandomString;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.user.User;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/employee/customer_service/create-customer")
public class CreateCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        HttpSession session = request.getSession(false);
//
//
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        User currentUser = (User) session.getAttribute("user");
//
//        if (currentUser.getRoleId() != 2) {
//            response.sendRedirect(request.getContextPath() + "/employee.customer_service/error-permission.jsp");
//            return;
//        }

        request.getRequestDispatcher("/employee/customer_service/create-customer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        User currentUser = (User) session.getAttribute("user");
//        if (currentUser.getRoleId() != 2) {
//            response.sendRedirect(request.getContextPath() + "/employee.customer_service/error-permission.jsp");
//            return;
//        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String birthDateStr = request.getParameter("birthDate");
        Date birthDate = null;

        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
                birthDate = Date.valueOf(birthDateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Định dạng ngày không hợp lệ: " + birthDateStr);
            }
        }
        Customer customer = new Customer();
        customer.setRoleId(7);
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setGender(gender);
        customer.setBirthDate(birthDate);
        customer.setAddress(address);
        customer.setActiveStatus(true);
        customer.setUserName(email);
        customer.setPasswordHash(RandomString.generateRandomString(12));
        customer.setPointLoyalty(0);


        CustomerDAO dao = new CustomerDAO();

        boolean isDuplicate = dao.isCustomerDuplicate(email);
        if (isDuplicate) {
            request.setAttribute("message", "⚠️ Email đã tồn tại!");
            request.setAttribute("messageType", "warning");
            request.getRequestDispatcher("/employee/customer_service/create-customer.jsp").forward(request, response);
            return;
        }

        boolean success = dao.insertCustomer(customer);
        if (success) {
            request.setAttribute("message", "✅ Thêm khách hàng thành công!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "❌ Không thể thêm khách hàng. Vui lòng thử lại.");
            request.setAttribute("messageType", "error");
        }
        request.getRequestDispatcher("/employee/customer_service/create-customer.jsp").forward(request, response);
    }
}
