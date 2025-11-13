package controller.customer;

import dao.carservice.ServiceRequestDAO;
import dao.customer.CustomerDAO; // DAO để lấy CustomerID
import dao.vehicle.VehicleDAO;
import model.dto.RepairJourneyView;     // DTO
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import service.diagnostic.CustomerDiagnosticService;
import model.vehicle.Vehicle;
import service.tracking.RepairTrackerService; // Service mới

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customer/repair-tracker")
public class CustomerRepairTrackerServlet extends HttpServlet {

    // Khởi tạo Service và DAO cần thiết
    private final RepairTrackerService trackerService = new RepairTrackerService();
    private final CustomerDAO customerDAO = new CustomerDAO(); // Giả sử bạn có DAO này
    private final CustomerDiagnosticService diagnosticService = new CustomerDiagnosticService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // 1. Lấy CustomerID từ Session User
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

            // 2. Lấy RequestID từ URL
            // URL sẽ có dạng: .../customer/repair-tracker?id=123
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID của quy trình.");
                return;
            }

            int requestID = Integer.parseInt(idParam);
            ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
            List<String> serviceName= serviceRequestDAO.getServiceNamesByRequestId(requestID);
            // 3. Gọi Service (KHÔNG gọi DAO) để lấy DTO (đã xử lý logic)
            RepairJourneyView journey = trackerService.getProcessedJourney(customerID, requestID);
            
            Vehicle vehicle = new Vehicle();
            VehicleDAO vehicleDAO = new VehicleDAO();
            int vehicleID = serviceRequestDAO.getServiceRequestById(requestID).getVehicleID();
            Vehicle vehicleInfo = vehicleDAO.getVehicleById(vehicleID);

            // 4. Kiểm tra kết quả
            if (journey == null) {
                // Có thể ID này không tồn tại, hoặc không thuộc về customer này
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy quy trình sửa chữa.");
                return;
            }

            // 5. Gửi DTO sang JSP
            request.setAttribute("serviceName", serviceName);
            request.setAttribute("journey", journey);
            request.setAttribute("vehicle", vehicleInfo);
            request.getRequestDispatcher("/view/customer/view-repair-tracker.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace(); // Nên log lỗi
            throw new ServletException("Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }
}