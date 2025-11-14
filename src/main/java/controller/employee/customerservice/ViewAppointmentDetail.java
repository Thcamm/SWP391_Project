package controller.employee.customerservice;

import dao.appointment.AppointmentDAO;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.appointment.Appointment;
import model.customer.Customer;
import model.user.User;
import service.appointment.AppointmentService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/customerservice/appointment-detail")
public class ViewAppointmentDetail extends HttpServlet {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AppointmentService appointmentService = new AppointmentService();
    // Khai báo FORMATTER cho LocalDateTime.parse() từ input datetime-local
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Xóa thông báo cũ (từ POST redirect)
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customerservice/appointment-list");
            return;
        }

        try {
            int appointmentID = Integer.parseInt(idStr);
            // Lấy chi tiết cuộc hẹn (Giả định DAO trả về DTO hoặc Appointment có đủ thông tin Khách hàng)
            Appointment appointmentDetail = appointmentDAO.getAppointmentById(appointmentID);
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customerDetail = customerDAO.getCustomerById(appointmentDetail.getCustomerID());
            if (appointmentDetail != null) {
                request.setAttribute("appointmentDetail", appointmentDetail);
                request.setAttribute("customerDetail", customerDetail);
            } else {
                session.setAttribute("message", "Appointment not found.");
                session.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid appointment ID format.");
            session.setAttribute("messageType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "An error occurred while fetching appointment details: " + e.getMessage());
            session.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("/view/customerservice/view-appointment-detail.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String redirectUrl = request.getParameter("redirectUrl"); // URL để redirect về trang detail hiện tại

        try {
            String appointmentIDStr = request.getParameter("appointmentID");

            if (appointmentIDStr == null || appointmentIDStr.isEmpty() || action == null) {
                throw new IllegalArgumentException("Missing required parameters.");
            }
            int appointmentID = Integer.parseInt(appointmentIDStr);

            // 1. Lấy chi tiết cuộc hẹn hiện tại (để có Description/CustomerID/Date cũ)
            Appointment currentAppointment = appointmentDAO.getAppointmentById(appointmentID);
            if (currentAppointment == null) {
                throw new IllegalArgumentException("Appointment not found in database.");
            }

            // Chuẩn bị model Appointment để update
            Appointment updateApm = new Appointment();
            updateApm.setAppointmentID(appointmentID);

            boolean updated = false;

            if ("reschedule_accept".equals(action)) {
                String newDateStr = request.getParameter("newAppointmentDate");
                String newDescription =  request.getParameter("newAppointmentDescription");
                if (newDateStr == null || newDateStr.isEmpty()) {
                    throw new IllegalArgumentException("Missing new appointment date/time.");
                }

                // Parse datetime-local string (YYYY-MM-DDTHH:MM)
                LocalDateTime newDate = LocalDateTime.parse(newDateStr, FORMATTER);

                updateApm.setAppointmentDate(newDate);
                // Đặt LocalDateTime
                updateApm.setStatus("ACCEPTED");
                if (newDescription != null && !newDescription.trim().isEmpty()) {
                    updateApm.setDescription(newDescription);
                } else {
                    updateApm.setDescription(currentAppointment.getDescription()); // Giữ mô tả cũ nếu trống
                }

                // Gọi Service/DAO để cập nhật (phương thức updateAppointment của bạn sẽ tăng RescheduleCount + 1)
                updated = appointmentService.updateAppointment(updateApm);

                if (updated) {
                    session.setAttribute("message", "✅ Appointment ID: " + appointmentID + " successfully Rescheduled and **Accepted**.");
                    session.setAttribute("messageType", "success");
                }

            } else if ("reject".equals(action)) {
                // Giữ nguyên ngày hẹn cũ
                updateApm.setAppointmentDate(currentAppointment.getAppointmentDate());
                updateApm.setStatus("REJECTED");

                updated = appointmentService.updateAppointment(updateApm);

                if (updated) {
                    session.setAttribute("message", "✅ Appointment ID: " + appointmentID + " successfully **Rejected**.");
                    session.setAttribute("messageType", "success");
                }
            } else {
                session.setAttribute("message", "🚫 Invalid or unsupported action.");
                session.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "⚠️ Invalid ID format.");
            session.setAttribute("messageType", "error");
        } catch (IllegalArgumentException e) {
            session.setAttribute("message", "🚫 " + e.getMessage());
            session.setAttribute("messageType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "⛔ An unexpected error occurred: " + e.getMessage());
            session.setAttribute("messageType", "error");
        }

        // Redirect về trang chi tiết hiện tại của CS
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = request.getContextPath() + "/customerservice/appointment-list";
        }
        response.sendRedirect(redirectUrl);
    }
}