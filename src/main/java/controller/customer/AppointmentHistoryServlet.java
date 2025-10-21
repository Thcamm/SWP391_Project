package controller.customer;

import dao.appointment.AppointmentDAO;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.appointment.Appointment;
import model.customer.Customer;
import model.user.User;
import service.appointment.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer/appointment-history")
public class AppointmentHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = null;
        try {
            customer = customerDAO.getCustomerByUserId(user.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        int customerId = customer.getCustomerId();
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String status = request.getParameter("status");
        String sortOrder = request.getParameter("sortOrder");
        List<Appointment> appointments;
        if ((fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty()) &&
                (status == null || status.isEmpty()) &&
                (sortOrder == null || sortOrder.isEmpty())) {
            appointments = appointmentDAO.getAppointmentsByCustomerId(customerId);
        } else {
            appointments = appointmentDAO.getAppointmentByFilter(customerId, fromDate, toDate, status, sortOrder);
        }
        try {
            request.setAttribute("statuses", appointmentDAO.getAllStatuses());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/view/customer/appointment-history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String appointmentIDStr = request.getParameter("appointmentID");
            String status = request.getParameter("status");

            if (appointmentIDStr == null || status == null || appointmentIDStr.isEmpty() || status.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                return;
            }

            int appointmentID = Integer.parseInt(appointmentIDStr);


            AppointmentDAO dao = new AppointmentDAO();
            AppointmentService service = new AppointmentService();

            boolean success = service.checkUpdateStatus(appointmentID, status);

            if (success) {
                dao.updateStatus(appointmentID, status);
                response.sendRedirect(request.getContextPath() + "/customer/appointment-history");
            } else {

                request.setAttribute("errorMessage", "Failed to update appointment status.");
                request.getRequestDispatcher("/view/customer/appointment-list.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating status: " + e.getMessage());
        }
    }

}
