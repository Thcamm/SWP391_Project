package controller.customer;

import dao.appointment.AppointmentDAO;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.appointment.Appointment;
import model.customer.Customer;
import service.appointment.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(urlPatterns = {"/customer/reschedule-appointment"})
public class RescheduleAppointment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private AppointmentService appointmentService = new AppointmentService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String appointmentIDRaw = request.getParameter("appointmentID");

        if (appointmentIDRaw != null && !appointmentIDRaw.isEmpty()) {
            try {
                int appointmentID = Integer.parseInt(appointmentIDRaw);
                Appointment appointment = new AppointmentDAO().getAppointmentById(appointmentID);

                if (appointment != null) {
                    Customer customer = new CustomerDAO().getCustomerById(appointment.getCustomerID());
                    request.setAttribute("appointment", appointment);
                    request.setAttribute("customer", customer);
                } else {
                    request.setAttribute("message", "Appointment not found.");
                    request.setAttribute("messageType", "error");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("message", "Invalid appointment ID format.");
                request.setAttribute("messageType", "error");
            } catch (SQLException e) {
                request.setAttribute("message", "Error retrieving customer information.");
                request.setAttribute("messageType", "error");
            }
        } else {
            request.setAttribute("message", "Missing appointment ID.");
            request.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("/view/customer/reschedule-appointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idRaw = request.getParameter("appointmentID");
        String newDateStr = request.getParameter("newAppointmentDate");
        String description = request.getParameter("description");

        if (idRaw == null || idRaw.isEmpty() || newDateStr == null || newDateStr.isEmpty()) {
            request.getSession().setAttribute("message", "Missing required parameters.");
            request.getSession().setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customer/appointment-history");
            return;
        }

        try {
            int appointmentID = Integer.parseInt(idRaw);
            LocalDateTime newDate = LocalDateTime.parse(newDateStr, FORMATTER);

            Appointment appointment = new Appointment();
            appointment.setAppointmentID(appointmentID);
            appointment.setAppointmentDate(newDate);
            appointment.setDescription(description);
            appointment.setStatus("PENDING");

            boolean updated = appointmentService.updateAppointment(appointment);

            if (updated) {
                request.getSession().setAttribute("message", "Appointment rescheduled successfully.");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Failed to update appointment.");
                request.getSession().setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("message", "Invalid appointment ID format.");
            request.getSession().setAttribute("messageType", "error");
        } catch (Exception e) {
            // Service sẽ ném Exception nếu vượt quá số lần reschedule
            request.getSession().setAttribute("message", e.getMessage());
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/customer/appointment-history");
    }
}
