package controller.employee.customerservice;

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

        HttpSession session = request.getSession();
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        SupportDAO dao = new SupportDAO();


        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int requestId = Integer.parseInt(idParam);
                SupportRequest supportRequest = dao.getSupportRequestById(requestId);

                if (supportRequest == null) {
                    response.sendError(404, "Support request not found.");
                    return;
                }

                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getCustomerById(supportRequest.getCustomerId());

                List<SupportCategory> categories = dao.getAllSupportCategories();
                Map<Integer, String> categoryMap = new HashMap<>();
                for (SupportCategory c : categories) {
                    categoryMap.put(c.getCategoryId(), c.getCategoryName());
                }

                request.setAttribute("supportRequest", supportRequest);
                request.setAttribute("customer", customer);
                request.setAttribute("categoryMap", categoryMap);

                request.getRequestDispatcher("/view/customerservice/support-detail.jsp")
                        .forward(request, response);
                return; // dừng doGet tại đây
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500, "Error loading support request detail.");
                return;
            }
        }

        // ======== Xử lý danh sách ========
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

            request.getRequestDispatcher("/view/customerservice/support-request-list.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Error loading support request list.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String requestIdParam = request.getParameter("requestId");
        String newStatus = request.getParameter("status");
        SupportService service = new SupportService();

        if (requestIdParam == null || newStatus == null) {
            session.setAttribute("message", "Missing parameters: requestId or status.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customerservice/view-support-request");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdParam);
            SupportDAO dao = new SupportDAO();

            boolean success = service.checkUpdateStatus(requestId, newStatus);

            if (success) {
                dao.updateSupportRequestStatus(requestId, newStatus);
                session.setAttribute("message", "Update support request status successfully.");
                session.setAttribute("messageType", "success");
            }

        } catch (Exception e) {
            session.setAttribute("message", e.getMessage());
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/customerservice/view-support-request");
    }

}
