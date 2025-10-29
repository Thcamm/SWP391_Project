package controller.employee.customerservice;

import common.utils.RandomString;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.Customer;
import util.MailService;

import java.io.IOException;
import java.sql.Date;

@WebServlet("/customerservice/create-customer")
public class CreateCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/customerservice/create-customer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String detailAddress = request.getParameter("addressDetail");
        String address = detailAddress + ", " + district + ", " + province;
        String birthDateStr = request.getParameter("birthDate");
        String password = RandomString.generateRandomString(12);
        Date birthDate = null;

        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
                birthDate = Date.valueOf(birthDateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format: " + birthDateStr);
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
        customer.setPasswordHash(util.PasswordUtil.hashPassword(password));
        customer.setPointLoyalty(0);

        CustomerDAO dao = new CustomerDAO();

        boolean isEmailDuplicate = dao.isEmailDuplicate(email);
        boolean isPhoneDuplicate = dao.isPhoneNumberDuplicate(phoneNumber);
        if (isEmailDuplicate && isPhoneDuplicate ) {
            request.setAttribute("message", "Email and phone already exists!");
            request.setAttribute("messageType", "warning");
            request.getRequestDispatcher("/view/customerservice/create-customer.jsp").forward(request, response);
            return;
        } else if (isEmailDuplicate) {
            request.setAttribute("message", "Email already exists!");
            request.setAttribute("messageType", "warning");
            request.getRequestDispatcher("/view/customerservice/create-customer.jsp").forward(request, response);
            return;
        } else if (isPhoneDuplicate) {
            request.setAttribute("message", "Phone already exists!");
            request.setAttribute("messageType", "warning");
            request.getRequestDispatcher("/view/customerservice/create-customer.jsp").forward(request, response);
            return;
        }

        boolean success = dao.insertCustomer(customer);
        if (success) {
            MailService.sendEmail(email,"You have been successfully created!","This is your temporary password " + password );
            request.setAttribute("message", "Customer added successfully! Password: " + password);
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Unable to add customer. Please try again.");
            request.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("/view/customerservice/create-customer.jsp").forward(request, response);
    }
}
