package controller.support;

import dao.customer.CustomerDAO;
import dao.support.SupportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.customer.Customer;
import model.support.SupportCategory;
import model.support.SupportRequest;
import model.user.User;
import model.workorder.ServiceRequest;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.logging.Level.parse;

@WebServlet("/customerservice/view-support-request")
@MultipartConfig
public class ViewSupportRequest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SupportDAO dao = new SupportDAO();

        String categoryParam = request.getParameter("categoryId"); // chú ý tên đúng với name trong JSP
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


            List<SupportRequest> supportrequests;
            if ((category == null ) && (statusList == null || statusList.length == 0)
                    && (fromDate == null || fromDate.isEmpty())
                    && (toDate == null || toDate.isEmpty())
                    && (sortOrder == null || sortOrder.isEmpty())) {

                supportrequests = dao.getAllSupportRequests();
            } else {
                supportrequests = dao.getFilteredSupportRequests(
                        category, statusList, fromDate, toDate, sortOrder
                );
            }

            Map<Integer, String> categoryMap = new HashMap<>();
            for (SupportCategory c : categories) {
                categoryMap.put(c.getCategoryId(), c.getCategoryName());
            }
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customerList = customerDAO.getAllCustomers();  // ← đây là customerList
            Map<Integer, String> customerEmailMap = new HashMap<>();

            for (Customer c : customerList) {
                customerEmailMap.put(c.getCustomerId(), c.getEmail());
            }

            request.setAttribute("customerEmailMap", customerEmailMap);

            Map<Integer, String> customerNameMap = new HashMap<>();

            for (Customer c : customerList) {
                customerNameMap.put(c.getCustomerId(), c.getFullName());
            }

            request.setAttribute("customerNameMap", customerNameMap);

            // 🔹 Gửi sang JSP
            request.setAttribute("supportrequests", supportrequests);
            request.setAttribute("categoryMap", categoryMap);
            request.setAttribute("categories", categories);
            request.setAttribute("statuses", statuses);

            request.getRequestDispatcher("/support/support_request_list.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi khi tải danh sách yêu cầu hỗ trợ.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String requestIdParam = request.getParameter("requestId");
        String newStatus = request.getParameter("status");

        if (requestIdParam == null || newStatus == null) {
            response.sendError(400, "Thiếu tham số requestId hoặc status.");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdParam);

            SupportDAO dao = new SupportDAO();

            // Nghiệp vụ thực tế: không cho update nếu đã CLOSED
            String currentStatus = dao.getCurrentStatus(requestId);
            if ("CLOSED".equalsIgnoreCase(currentStatus) || "RESOLVED".equalsIgnoreCase(currentStatus)) {
                request.setAttribute("error", "Không thể thay đổi trạng thái khi yêu cầu đã CLOSED.");
                doGet(request, response);
                return;
            }

            dao.updateSupportRequestStatus(requestId, newStatus);
            response.sendRedirect(request.getContextPath() + "/customerservice/view-support-request");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi khi cập nhật trạng thái yêu cầu hỗ trợ!");
        }
    }
}
