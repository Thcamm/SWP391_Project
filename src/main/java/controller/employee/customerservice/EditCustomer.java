package controller.employee.customerservice;

import common.utils.PaginationUtils;
import dao.customer.PendingChangeDAO;
import dao.customer.CustomerDAO;
import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import model.customer.Customer;
import model.customer.PendingChange;
import model.dto.RepairJourneySummaryDTO;
import model.vehicle.CarBrand;
import model.vehicle.Vehicle;
import service.tracking.RepairTrackerService;
import util.MailService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(urlPatterns = {"/customerservice/customer-detail"})
public class EditCustomer extends HttpServlet {
    private static final int VEHICLES_PER_PAGE = 5;
    private static final int JOURNEYS_PER_PAGE = 10;

    private final PendingChangeDAO pendingChangeDAO = new PendingChangeDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CarDataDAO carDAO = new CarDataDAO();
    private final RepairTrackerService repairTrackerService = new RepairTrackerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // 📨 Lấy message hiển thị 1 lần
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ!");
            return;
        }

        try {
            int customerId = Integer.parseInt(idParam);

            // 🔹 Thông tin khách hàng
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy khách hàng!");
                return;
            }
            request.setAttribute("customer", customer);

            // === PHÂN TRANG XE (VEHICLE) ===
            int vehicleCurrentPage = 1;
            String vehiclePageParam = request.getParameter("vehiclePage");
            if (vehiclePageParam != null) {
                try {
                    vehicleCurrentPage = Math.max(1, Integer.parseInt(vehiclePageParam));
                } catch (NumberFormatException ignored) {}
            }

            int totalVehicles = vehicleDAO.getVehicleCountByCustomerId(customerId);
            PaginationUtils.PaginationCalculation vehicleCalc =
                    PaginationUtils.calculateParams(totalVehicles, vehicleCurrentPage, VEHICLES_PER_PAGE);

            List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerIdPaginated(
                    customerId, VEHICLES_PER_PAGE, vehicleCalc.getOffset());

            request.setAttribute("vehicles", vehicles);
            request.setAttribute("vehicleTotalPages", vehicleCalc.getTotalPages());
            request.setAttribute("vehicleCurrentPage", vehicleCalc.getSafePage());

            // === PHÂN TRANG HÀNH TRÌNH SỬA CHỮA (JOURNEY) ===
            int journeyCurrentPage = 1;
            String journeyPageParam = request.getParameter("journeyPage");
            if (journeyPageParam != null) {
                try {
                    journeyCurrentPage = Math.max(1, Integer.parseInt(journeyPageParam));
                } catch (NumberFormatException ignored) {}
            }

            int totalJourneys = repairTrackerService.countSummariesForCustomer(customerId);
            PaginationUtils.PaginationCalculation journeyCalc =
                    PaginationUtils.calculateParams(totalJourneys, journeyCurrentPage, JOURNEYS_PER_PAGE);

            int offset = journeyCalc.getOffset();
            List<RepairJourneySummaryDTO> journeyData =
                    repairTrackerService.getPaginatedSummariesForCustomer(customerId, JOURNEYS_PER_PAGE, offset);

            PaginationUtils.PaginationResult<RepairJourneySummaryDTO> journeyResult =
                    new PaginationUtils.PaginationResult<>(
                            journeyData,
                            totalJourneys,
                            journeyCalc.getTotalPages(),
                            journeyCalc.getSafePage(),
                            JOURNEYS_PER_PAGE
                    );

            request.setAttribute("journeyList", journeyResult);
            request.setAttribute("journeyTotalPages", journeyCalc.getTotalPages());
            request.setAttribute("journeyCurrentPage", journeyCalc.getSafePage());

            // === DỮ LIỆU HÃNG XE ===
            List<CarBrand> brands = carDAO.getAllBrands();
            request.setAttribute("brands", brands);

            // === FORWARD ===
            request.getRequestDispatcher("/view/customerservice/customer-detail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID phải là số!");
        } catch (SQLException e) {
            throw new ServletException("Lỗi truy vấn dữ liệu khách hàng", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int customerId = Integer.parseInt(request.getParameter("customerId"));
        Map<String, String> changes = new HashMap<>();

        String fullName = request.getParameter("FullName");
        String email = request.getParameter("Email");
        String phone = request.getParameter("PhoneNumber");
        String gender = request.getParameter("Gender");
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String addressDetail = request.getParameter("addressDetail");
        String newPassword = request.getParameter("NewPassword");

        String address = String.join(", ",
                Arrays.asList(addressDetail, district, province).stream()
                        .filter(s -> s != null && !s.isBlank()).toList());

        if (fullName != null && !fullName.isEmpty()) changes.put("FullName", fullName);
        if (email != null && !email.isEmpty()) changes.put("Email", email);
        if (phone != null && !phone.isEmpty()) changes.put("PhoneNumber", phone);
        if (gender != null && !gender.isEmpty()) changes.put("Gender", gender);
        if (address != null && !address.isEmpty()) changes.put("Address", address);
        if (newPassword != null && !newPassword.isEmpty()) changes.put("NewPassword", newPassword);

        HttpSession session = request.getSession();
        if (changes.isEmpty()) {
            session.setAttribute("message", "Không có trường nào thay đổi.");
            session.setAttribute("messageType", "warning");
            response.sendRedirect(request.getContextPath() + "/customerservice/customer-detail?id=" + customerId);
            return;
        }

        try {
            PendingChange pending = new PendingChange();
            pending.setCustomerId(customerId);
            pending.setFieldsChanged(changes);
            pending.setToken(UUID.randomUUID().toString());

            pendingChangeDAO.createPendingChange(customerId, pending);

            String customerEmail = customerDAO.getCustomerById(customerId).getEmail();
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" +
                    request.getServerPort() + request.getContextPath();

            String confirmLink = baseUrl + "/verify-change?token=" + pending.getToken();
            String rejectLink = baseUrl + "/reject-change?token=" + pending.getToken();

            StringBuilder html = new StringBuilder("<p>CS staff đã yêu cầu cập nhật thông tin của bạn:</p><ul>");
            changes.forEach((k, v) -> html.append("<li>").append(k).append(": ").append(v).append("</li>"));
            html.append("</ul><p>Xác nhận để thay đổi được áp dụng:</p>")
                    .append("<a href='").append(confirmLink).append("'>Xác nhận</a> | ")
                    .append("<a href='").append(rejectLink).append("'>Hủy</a>")
                    .append("<p>Token hết hạn sau 24h nếu không xác nhận.</p>");

            MailService.sendHtmlEmail(customerEmail, "Xác nhận thay đổi thông tin", html.toString());

            session.setAttribute("message", "Yêu cầu thay đổi đã gửi email xác nhận cho khách hàng.");
            session.setAttribute("messageType", "success");

            response.sendRedirect(request.getContextPath() + "/customerservice/customer-detail?id=" + customerId);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Lỗi khi gửi yêu cầu thay đổi: " + e.getMessage());
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/customerservice/customer-detail?id=" + customerId);
        }
    }
}
