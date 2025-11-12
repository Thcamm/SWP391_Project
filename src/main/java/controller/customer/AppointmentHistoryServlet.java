package controller.customer;

import common.utils.PaginationUtils;
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
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
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
        int currentPage = 1;
        int itemsPerPage = 10;

        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {
            }
        }
        PaginationUtils.PaginationResult<Appointment> result;
        List<Appointment> appointments;
        if ((fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty()) &&
                (status == null || status.isEmpty()) &&
                (sortOrder == null || sortOrder.isEmpty())) {
            int totalItems = appointmentDAO.countAppointmentsByCustomerId(customerId);
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            appointments = appointmentDAO.getAppointmentsByCustomerId(customerId, itemsPerPage, calc.getOffset());
            result = new PaginationUtils.PaginationResult<>(
                    appointments, totalItems, calc.getTotalPages(),calc.getSafePage(), itemsPerPage);
        } else {
            int totalItems = appointmentDAO.countAppointmentsByFilter(customerId, fromDate, toDate, status, sortOrder);
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);
            appointments = appointmentDAO.getAppointmentByFilter(customerId, fromDate, toDate, status, sortOrder, itemsPerPage, calc.getOffset());
            result = new PaginationUtils.PaginationResult<>(
                    appointments, totalItems, calc.getTotalPages(), calc.getSafePage(), itemsPerPage);
        }
        try {
            request.setAttribute("statuses", appointmentDAO.getAllStatuses());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("appointments", result);
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
                request.setAttribute("message", "Update appointment status successfully.");
                request.setAttribute("messageType", "success");
                response.sendRedirect(request.getContextPath() + "/customer/appointment-history");
            } else {

                request.setAttribute("message", "Failed to update appointment status.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/view/customer/appointment-list.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error updating status: " + e.getMessage());
        }
    }

}
