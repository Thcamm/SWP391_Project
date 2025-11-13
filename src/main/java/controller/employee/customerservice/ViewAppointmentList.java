package controller.employee.customerservice;

import common.utils.PaginationUtils;
import dao.appointment.AppointmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.appointment.AppointmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/customerservice/appointment-list")
public class ViewAppointmentList extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
        String name = request.getParameter("searchName");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String[] statusList = request.getParameterValues("status");
        String sortOrder = request.getParameter("sortOrder");

        int currentPage = 1;
        int itemsPerPage = 10;

        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {
            }
        }
        AppointmentDAO dao = new AppointmentDAO();
        List<Map<String, Object>> appointments;
        PaginationUtils.PaginationResult<Map<String, Object>> result;
        if ((name == null || name.isEmpty()) &&
                (fromDate == null || fromDate.isEmpty()) &&
                (toDate == null || toDate.isEmpty()) &&
                (statusList == null || statusList.length == 0) &&
                (sortOrder == null || sortOrder.isEmpty())) {
            int totalItems = dao.countAppointment();
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            appointments = dao.getAllAppointmentsWithLimit(itemsPerPage, calc.getOffset());
            result = new PaginationUtils.PaginationResult<>(
                    appointments, totalItems, calc.getTotalPages(),
                    calc.getSafePage(), itemsPerPage);

        } else {
            int totalItems = 0;
            try {
                totalItems = dao.countSearchAppointment(name, fromDate, toDate, statusList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            try {
                appointments = dao.searchAppointmentWithLimit(
                        name, fromDate, toDate, statusList, sortOrder,
                        itemsPerPage, calc.getOffset());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            result = new PaginationUtils.PaginationResult<>(
                    appointments, totalItems, calc.getTotalPages(),
                    calc.getSafePage(), itemsPerPage);
        }
        request.setAttribute("appointmentList", result);
        request.setAttribute("currentPage", result.getCurrentPage());
        request.setAttribute("totalPages", result.getTotalPages());
        request.getRequestDispatcher("/view/customerservice/appointment-list.jsp")
                .forward(request, response);

    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        try {
            String appointmentIDStr = request.getParameter("appointmentID");
            String status = request.getParameter("status");

            if (appointmentIDStr == null || status == null || appointmentIDStr.isEmpty() || status.isEmpty()) {
                session.setAttribute("message", "Missing parameters: appointmentID or status.");
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/customerservice/appointment-list");
                return;
            }

            int appointmentID = Integer.parseInt(appointmentIDStr);

            AppointmentDAO dao = new AppointmentDAO();
            AppointmentService service = new AppointmentService();

            boolean success = service.checkUpdateStatus(appointmentID, status);

            if (success) {
                dao.updateStatus(appointmentID, status);
                session.setAttribute("message", "Update appointment status successfully.");
                session.setAttribute("messageType", "success");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", e.getMessage());
            session.setAttribute("messageType", "error");
        }

        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = request.getContextPath() + "/customerservice/appointment-list";
        }
        response.sendRedirect(redirectUrl);

    }
}

