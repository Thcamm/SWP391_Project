package controller.customerservice;

import dao.customer.CustomerDAO;
import dao.support.SupportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.customer.Customer;
import model.support.SupportCategory;
import model.support.SupportRequest;
import service.support.SupportService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customerservice/view-support-request")
@MultipartConfig
public class ViewSupportRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SupportDAO dao = new SupportDAO();

        String categoryParam = request.getParameter("categoryId");
        Integer category = null;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                category = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                category = null;
            }
        }

        String[] statusList = request.getParameterValues("statuses");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String sortOrder = request.getParameter("sortOrder");

        try {
            List<SupportCategory> categories = dao.getAllSupportCategories();
            List<String> statuses = dao.getAllStatuses();

            List<SupportRequest> supportRequests;
            if ((category == null) && (statusList == null || statusList.length == 0)
                    && (fromDate == null || fromDate.isEmpty())
                    && (toDate == null || toDate.isEmpty())
                    && (sortOrder == null || sortOrder.isEmpty())) {
                supportRequests = dao.getAllSupportRequests();
            } else {
                supportRequests = dao.getFilteredSupportRequests(
                        category, statusList, fromDate, toDate, sortOrder
                );
            }

            Map<Integer, String> categoryMap = new HashMap<>();
            for (SupportCategory c : categories) {
                categoryMap.put(c.getCategoryId(), c.getCategoryName());
            }

            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customerList = customerDAO.getAllCustomers();
            Map<Integer, String> customerEmailMap = new HashMap<>();
            Map<Integer, String> customerNameMap = new HashMap<>();

            for (Customer c : customerList) {
                customerEmailMap.put(c.getCustomerId(), c.getEmail());
                customerNameMap.put(c.getCustomerId(), c.getFullName());
            }

            request.setAttribute("customerEmailMap", customerEmailMap);
            request.setAttribute("customerNameMap", customerNameMap);
            request.setAttribute("supportrequests", supportRequests);
            request.setAttribute("categoryMap", categoryMap);
            request.setAttribute("categories", categories);
            request.setAttribute("statuses", statuses);

            request.getRequestDispatcher("/view/customerservice/support_request_list.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Error loading support request list.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String requestIdParam = request.getParameter("requestId");
        String newStatus = request.getParameter("status");
        SupportService service = new SupportService();

        if (requestIdParam == null || newStatus == null) {
            response.sendError(400, "Missing parameters: requestId or status.");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdParam);
            SupportDAO dao = new SupportDAO();

            String currentStatus = dao.getCurrentStatus(requestId);
            boolean success = service.checkUpdateStatus(requestId, currentStatus);

            if (success) {
                dao.updateSupportRequestStatus(requestId, newStatus);
                response.sendRedirect(request.getContextPath() + "/customerservice/view-support-request");
            } else {
                request.setAttribute("errorMessage", "Failed to update support request status.");
                request.getRequestDispatcher("/view/customerservice/support_request_list.jsp")
                        .forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Error updating support request status.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
