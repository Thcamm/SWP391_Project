package controller.employee.customerservice;

import dao.appointment.AppointmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.appointment.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/customerservice/appointment-list")
public class AppointmentListServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("searchName");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        String[] statusList = request.getParameterValues("status");

        String sortOrder = request.getParameter("sortOrder");
        AppointmentDAO dao = new AppointmentDAO();
        List<Map<String, Object>> appointments;

        if ((name == null || name.isEmpty()) &&
                (fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty()) &&
                (statusList == null || statusList.length == 0) &&
                (sortOrder == null || sortOrder.isEmpty())) {

            appointments = dao.getAllAppointments();

        } else {
            try {
                appointments = dao.searchAppointment(name,fromDate, toDate, statusList, sortOrder);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/view/customerservice/appointment-list.jsp")
                .forward(request, response);

    }
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response)
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
                response.sendRedirect(request.getContextPath() + "/customerservice/appointment-list");
            } else {
                request.setAttribute("errorMessage", "Failed to update appointment status.");
                request.getRequestDispatcher("/view/customerservice/appointment-list.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating status: " + e.getMessage());
        }
    }
    }


