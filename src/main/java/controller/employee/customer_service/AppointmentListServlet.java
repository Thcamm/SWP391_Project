package controller.employee.customer_service;

import dao.appointment.AppointmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.appointment.Appointment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer_service/appointment-list")
public class AppointmentListServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//
//        if (session == null || session.getAttribute("user") == null) {
//            String currentURL = request.getRequestURL().toString();
//            String queryString = request.getQueryString();
//            if (queryString != null) {
//                currentURL += "?" + queryString;
//            }
//
//            session = request.getSession(true);
//            session.setAttribute("redirectAfterLogin", currentURL);
//
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        User currentUser = (User) session.getAttribute("user");
//
//        if (currentUser.getRoleId() != 2) {
//            response.sendRedirect(request.getContextPath() + "/employee.customer_service/error-permission.jsp");
//            return;
//        }
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        String[] statusList = request.getParameterValues("status");

        String sortOrder = request.getParameter("sortOrder");
        AppointmentDAO dao = new AppointmentDAO();
        List<Appointment> appointments;

        if ((fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty()) &&
                (statusList == null || statusList.length == 0) &&
                (sortOrder == null || sortOrder.isEmpty())) {

            appointments = dao.getAllAppointments();

        } else {
            try {
                appointments = dao.searchAppointment(fromDate, toDate, statusList, sortOrder);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/employee/customer_service/appointment-list.jsp")
                .forward(request, response);

    }


}
