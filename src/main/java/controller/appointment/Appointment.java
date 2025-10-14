package controller.appointment;

import dao.appointment.AppointmentDAO;
import dao.customer.CustomerDAO;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import model.vehicle.Vehicle;

import java.io.IOException;

@WebServlet(name = "AppointmentService", urlPatterns = {"/AppointmentService"})
public class Appointment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/plain;charset=UTF-8");
        //response.getWriter().write("AppointmentService GET OK");
        request.getRequestDispatcher("appointment-scheduling.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: handle POST
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 1. Kiểm tra người dùng đã đăng nhập chưa
        if (user == null) {
            response.sendRedirect("login.jsp"); // Chuyển đến trang đăng nhập
            return; // Dừng thực thi ngay lập tức
        }

        // 2. Lấy các tham số cần thiết
        String carBrand = request.getParameter("carBrand");
        String licensePlate = request.getParameter("licensePlate");
        String dateStr = request.getParameter("appointmentDate");
        String description = request.getParameter("description");

        // (Tùy chọn) Validation: Kiểm tra các trường bắt buộc không bị trống
        if (licensePlate == null || licensePlate.trim().isEmpty() || dateStr == null || dateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "License plate and date are required.");
            request.getRequestDispatcher("appointment-scheduling.jsp").forward(request, response);
            return;
        }

        // 3. Khởi tạo các DAO và Service
        CustomerDAO customerDAO = new CustomerDAO();
        VehicleDAO vehicleDAO = new VehicleDAO(); // Cần có VehicleDAO
        AppointmentDAO appointmentDAO = new AppointmentDAO(); // Hoặc dùng Service

        try {
            // 4. Lấy CustomerID từ UserID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());
            if (customerID == -1) {
                // Có thể xử lý trường hợp user có tồn tại nhưng chưa có record trong bảng Customer
                // Ví dụ: Tạo mới customer record rồi lấy ID
                throw new ServletException("Customer profile not found for the logged-in user.");
            }

// 5. Xử lý Vehicle: Tìm hoặc Tạo mới
            int vehicleID = vehicleDAO.getVehicleIdByLicensePlate(licensePlate);
            if (vehicleID == -1) {
                // Nếu xe chưa tồn tại, tạo xe mới
                Vehicle newVehicle = new Vehicle();
                newVehicle.setCustomerID(customerID);
                newVehicle.setLicensePlate(licensePlate);
                newVehicle.setBrand(carBrand);
                // Không cần set vehicleID thủ công

                vehicleID = vehicleDAO.insertVehicle(newVehicle); // ← Lấy ID từ return

                if (vehicleID == -1) {
                    throw new ServletException("Failed to insert vehicle");
                }
            }

            // 6. Xử lý AppointmentService
            model.appointment.Appointment appointment = new model.appointment.Appointment();
            appointment.setCustomerID(customerID);
            appointment.setVehicleID(vehicleID); // QUAN TRỌNG: Gán VehicleID
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dateStr);
            appointment.setAppointmentDate(dateTime.toLocalDate());// Đặt trong try-catch
            appointment.setDescription(description);
            appointment.setStatus("CONFIRM"); // Gán trạng thái ban đầu

            // 7. Lưu vào cơ sở dữ liệu
            appointmentDAO.insertAppointment(appointment);

            // 8. Chuyển hướng sau khi thành công (PRG Pattern)
            request.setAttribute("successMessage", "AppointmentService scheduled successfully!");
            request.getRequestDispatcher("appointment-scheduling.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            // Bắt lỗi nếu định dạng ngày sai
            request.setAttribute("errorMessage", "Invalid date format. Please use YYYY-MM-DD.");
            request.getRequestDispatcher("appointment-scheduling.jsp").forward(request, response);
        } catch (Exception e) {
            // Bắt các lỗi khác (lỗi DB, etc.)
            e.printStackTrace(); // Ghi log lỗi ra console
            // Hiển thị lỗi chi tiết trên màn hình
            String detailError = "An error occurred: " + e.getMessage() + " | Type: " + e.getClass().getSimpleName();
            request.setAttribute("errorMessage", detailError);
            request.getRequestDispatcher("appointment-scheduling.jsp").forward(request, response);
        }
    }
}