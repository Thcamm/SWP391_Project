package controller.customer;

import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.Customer;

import java.io.IOException;
import java.util.List;


@WebServlet("/search-customer")
public class SearchCustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String contact = request.getParameter("contact");
        String licensePlate = request.getParameter("licensePlate");

        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers = dao.searchCustomers(fullName, contact, licensePlate);

        request.setAttribute("customerList", customers);
        request.getRequestDispatcher("search-customer.jsp").forward(request, response);
    }
}

