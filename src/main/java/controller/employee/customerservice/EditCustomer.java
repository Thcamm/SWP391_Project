package controller.employee.customerservice;

import common.utils.PaginationUtils;
import dao.customer.PendingChangeDAO;
import dao.customer.CustomerDAO;
import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.annotation.WebServlet;
import model.customer.Customer;
import model.customer.PendingChange;
import model.dto.RepairJourneySummaryDTO;
import model.vehicle.CarBrand;
import model.vehicle.Vehicle;
import util.MailService;
import model.dto.RepairJourneySummaryDTO;
import service.tracking.RepairTrackerService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@WebServlet(urlPatterns = {"/customerservice/customer-detail"})
public class EditCustomer extends HttpServlet {
    private static final int VEHICLES_PER_PAGE = 5;
    private PendingChangeDAO pendingChangeDAO = new PendingChangeDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private CarDataDAO carDAO = new CarDataDAO();
    private final RepairTrackerService repairListService = new RepairTrackerService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
        String id = request.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ!");
            return;
        }

        try {
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    currentPage = 1; // Mặc định là 1 nếu param không hợp lệ
                }
            }
            int customerId = Integer.parseInt(id);
            Customer customer = customerDAO.getCustomerById(customerId);
            request.setAttribute("customer", customer);
            int totalVehicles = vehicleDAO.getVehicleCountByCustomerId(customerId);

            // Tính toán phân trang bằng tiện ích của bạn
            PaginationUtils.PaginationCalculation pagination = PaginationUtils.calculateParams(
                    totalVehicles,
                    currentPage,
                    VEHICLES_PER_PAGE
            );

            // Lấy danh sách xe cho trang hiện tại
            List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerIdPaginated(
                    customerId,
                    VEHICLES_PER_PAGE,
                    pagination.getOffset()
            );
            List<RepairJourneySummaryDTO> journeyList =
                    repairListService.getSummariesForCustomer(customerId);

            // 3. Gửi danh sách này sang JSP
            request.setAttribute("journeyList", journeyList);
            request.setAttribute("vehicles", vehicles); // Danh sách xe của trang này
            request.setAttribute("totalPages", pagination.getTotalPages()); // Gửi tổng số trang
            request.setAttribute("currentPage", pagination.getSafePage());
            List<CarBrand> brands = carDAO.getAllBrands();
            request.setAttribute("brands", brands);
            request.getRequestDispatcher("/view/customerservice/customer-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID phải là số!");
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int customerId = Integer.parseInt(request.getParameter("customerId"));
        Map<String, String> changes = new HashMap<>();

        // Lấy dữ liệu từ form
        String fullName = request.getParameter("FullName");
        String email = request.getParameter("Email");
        String phone = request.getParameter("PhoneNumber");
        String gender = request.getParameter("Gender");
        String province =  request.getParameter("province");
        String district  =  request.getParameter("district");
        String addressDetail =  request.getParameter("addressDetail");

        String newPassword = request.getParameter("NewPassword");
        String address = addressDetail + ", " + district + ", " + province;
        if (fullName != null && !fullName.isEmpty()) changes.put("FullName", fullName);
        if (email != null && !email.isEmpty()) changes.put("Email", email);
        if (phone != null && !phone.isEmpty()) changes.put("PhoneNumber", phone);
        if (gender != null && !gender.isEmpty()) changes.put("Gender", gender);
        if (address != null && !address.isEmpty()) changes.put("Address", address);
        if (newPassword != null && !newPassword.isEmpty()) changes.put("NewPassword", newPassword);

        if (!changes.isEmpty()) {
            try {
                PendingChange pending = new PendingChange();
                pending.setCustomerId(customerId);
                pending.setFieldsChanged(changes);
                pending.setToken(UUID.randomUUID().toString());
                pending.setTokenExpiry(null); // DB set +24h

                pendingChangeDAO.createPendingChange(customerId, pending);

                String customerEmail = customerDAO.getCustomerById(customerId).getEmail();
                String confirmLink = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath() + "/verify-change?token=" + pending.getToken();

                String rejectLink = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath() + "/reject-change?token=" + pending.getToken();

                StringBuilder htmlContent = new StringBuilder("<p>CS staff đã yêu cầu cập nhật thông tin của bạn:</p><ul>");
                changes.forEach((k,v)-> htmlContent.append("<li>").append(k).append(": ").append(v).append("</li>"));
                htmlContent.append("</ul>");
                htmlContent.append("<p>Xác nhận để thay đổi được áp dụng:</p>");
                htmlContent.append("<a href='").append(confirmLink).append("'>Xác nhận</a> | ");
                htmlContent.append("<a href='").append(rejectLink).append("'>Hủy</a>");
                htmlContent.append("<p>Token hết hạn sau 24h nếu không xác nhận.</p>");

                MailService.sendHtmlEmail(customerEmail, "Xác nhận thay đổi thông tin", htmlContent.toString());

                // **Set session message để hiển thị 1 lần**
                HttpSession session = request.getSession();
                session.setAttribute("message", "Yêu cầu thay đổi đã gửi email xác nhận khách hàng.");
                session.setAttribute("messageType", "success");

                // **Redirect sang GET với customerId**
                response.sendRedirect(request.getContextPath() + "/customerservice/customer-detail?id=" + customerId);

            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().println("Lỗi khi tạo PendingChange: " + e.getMessage());
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("message", "Không có trường nào thay đổi.");
            session.setAttribute("messageType", "warning");
            response.sendRedirect(request.getContextPath() + "/customerservice/customer-detail?id=" + customerId);
        }
    }
}
