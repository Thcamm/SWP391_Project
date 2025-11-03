package controller.employee.techmanager;

import dao.workorder.RejectedTaskDAO;
import model.employee.techmanager.RejectedTaskDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for displaying rejected tasks that need reassignment
 */
@WebServlet("/techmanager/rejected-tasks")
public class RejectedTasksServlet extends HttpServlet {

    private final RejectedTaskDAO rejectedTaskDAO = new RejectedTaskDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get rejected tasks from DAO
            List<RejectedTaskDTO> rejectedTasks = rejectedTaskDAO.getRejectedTasks();

            // Set attribute and forward to JSP
            request.setAttribute("rejectedTasks", rejectedTasks);
            request.getRequestDispatcher("/view/techmanager/rejected-tasks.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading rejected tasks: " + e.getMessage());
        }
    }
}
