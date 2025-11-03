package controller.employee.technician;

import common.message.ServiceResult;
import common.utils.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TaskStatistics;
import model.employee.technician.TechnicianActivity;
import model.pagination.PaginationResponse;
import service.employee.TechnicianService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet("/technician/home")
public class TechnicianHomeServlet extends HttpServlet {
    private final TechnicianService technicianService = new TechnicianService();

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);

        if(techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        System.out.println("Session: " + session);
        if (session != null) {
            System.out.println("userId in session = " + session.getAttribute("userId"));
        }

        Employee techinician = techResult.getData(Employee.class);

        int newTasksPage = getPageParameter(req, "newTasksPage", DEFAULT_PAGE);
        int inProgressPage = getPageParameter(req, "inProgressPage", DEFAULT_PAGE);
        int activitiesPage = getPageParameter(req, "activitiesPage", DEFAULT_PAGE);

        ServiceResult statsResult = technicianService.getTaskStatistics(techinician.getEmployeeId());
        TaskStatistics statistics = statsResult.getData(TaskStatistics.class);

        PaginationResponse<TaskAssignment> newTasks = technicianService.getNewAssignedTasks(
                techinician.getEmployeeId(),
                newTasksPage,
                DEFAULT_PAGE_SIZE
        );

        PaginationResponse<TaskAssignment> inProgressTasks = technicianService.getInProgressTasks(
                techinician.getEmployeeId(),
                inProgressPage,
                DEFAULT_PAGE_SIZE
        );

        PaginationResponse<TechnicianActivity> recentActivities = technicianService.getRecentActivities(
                techinician.getEmployeeId(),
                activitiesPage,
                DEFAULT_PAGE_SIZE
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

// Định dạng cho danh sách task mới
        for (TaskAssignment task : newTasks.getData()) {
            if (task.getAssignedDate() != null) {
                task.setAssignedDateFormatted(task.getAssignedDate().format(formatter));
            }
        }

// Định dạng cho danh sách task đang làm
        for (TaskAssignment task : inProgressTasks.getData()) {
            if (task.getAssignedDate() != null) {
                task.setAssignedDateFormatted(task.getAssignedDate().format(formatter));
            }
        }

        req.setAttribute("technician", techinician);
        req.setAttribute("statistics", statistics);
        req.setAttribute("newTasks", newTasks);
        req.setAttribute("inProgressTasks", inProgressTasks);
        req.setAttribute("recentActivities", recentActivities);

        req.getRequestDispatcher( "/view/technician/home.jsp").forward(req, resp);
    }

    private int getPageParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String pageStr = req.getParameter(paramName);
        if(pageStr != null) {
            try{
                int page = Integer.parseInt(pageStr);
                return page > 0 ? page : defaultValue;
            }catch (NumberFormatException e){
                return defaultValue;
            }
        }

        return defaultValue;
    }
}
