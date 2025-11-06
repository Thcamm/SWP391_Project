package controller.employee.customerservice;

import common.utils.PaginationUtils;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.customer.Customer;

import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/search-customer")
public class SearchCustomer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("searchName");
        String licensePlate = request.getParameter("searchLicensePlate");
        String emailOrPhone = request.getParameter("searchEmail");
        String sortOrder = request.getParameter("sortOrder");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        int currentPage = 1;
        int itemsPerPage = 10;

        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {
            }
        }

        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers;
        PaginationUtils.PaginationResult<Customer> result;

        boolean isFilterEmpty =
                (name == null || name.isEmpty()) &&
                        (licensePlate == null || licensePlate.isEmpty()) &&
                        (emailOrPhone == null || emailOrPhone.isEmpty()) &&
                        (fromDate == null || fromDate.isEmpty()) &&
                        (toDate == null || toDate.isEmpty());

        if (isFilterEmpty) {
            int totalItems = dao.countCustomers();
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            customers = dao.getCustomersWithLimit(itemsPerPage, calc.getOffset());
            result = new PaginationUtils.PaginationResult<>(
                    customers, totalItems, calc.getTotalPages(),
                    calc.getSafePage(), itemsPerPage);
        } else {
            int totalItems = dao.countSearchCustomers(name, emailOrPhone, licensePlate, fromDate, toDate);
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            customers = dao.searchCustomersWithLimit(
                    name, emailOrPhone, licensePlate, sortOrder,
                    fromDate, toDate, itemsPerPage, calc.getOffset());

            result = new PaginationUtils.PaginationResult<>(
                    customers, totalItems, calc.getTotalPages(),
                    calc.getSafePage(), itemsPerPage);
        }

        request.setAttribute("customerList", result);
        request.setAttribute("currentPage", result.getCurrentPage());
        request.setAttribute("totalPages", result.getTotalPages());

        request.getRequestDispatcher("/view/customerservice/search-customer.jsp")
                .forward(request, response);
    }
}
