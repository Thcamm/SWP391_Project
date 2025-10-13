package controller.employee.customer_service;
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
import java.util.List;


@WebServlet("/customer_service/search-customer")
public class SearchCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        HttpSession session = request.getSession(false);
//
//         if (session == null || session.getAttribute("user") == null) {
//            String currentURL = request.getRequestURL().toString();
//            String queryString = request.getQueryString();
//            if (queryString != null) {
//                currentURL += "?" + queryString;
//            }
//
//            session = request.getSession(true);
//            session.setAttribute("redirectAfterLogin", currentURL);
//
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

        String name = request.getParameter("searchName");
        String licensePlate = request.getParameter("searchLicensePlate");
        String emailOrPhone = request.getParameter("searchEmail");
        String sortOrder = request.getParameter("sortOrder");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers ;
        if ((name == null || name.isEmpty()) &&
                (licensePlate == null || licensePlate.isEmpty()) &&
                (emailOrPhone == null || emailOrPhone.isEmpty()) &&
                (fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty())) {

            customers = dao.getAllCustomers(sortOrder);
        } else {
            customers = dao.searchCustomers(name, emailOrPhone, licensePlate, sortOrder, fromDate, toDate);
        }

        request.setAttribute("customerList", customers);

        request.getRequestDispatcher("/employee/customer_service/search-customer.jsp").forward(request, response);
    }
}
