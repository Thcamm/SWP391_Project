package controller.employee.technician;

import common.message.ServiceResult;
import common.utils.MessageHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TaskStatistics;
import model.pagination.PaginationResponse;
import service.employee.TechnicianService;
import service.vehicle.VehicleDiagnosticService;

import java.time.format.DateTimeFormatter;


@WebServlet("/technician/tasks")
public class AllTasksServlet extends HttpServlet {

    private final TechnicianService technicianService = new TechnicianService();

    private final VehicleDiagnosticService vehicleDiagnosticService = new VehicleDiagnosticService();
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 5;

    public AllTasksServlet() {
        super();
    }

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        vehicleDiagnosticService.autoRejectExpiredDiagnostics(10);
        HttpSession session = req.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);

        if(techResult.isError()){
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath()+"/technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);

        String status = req.getParameter("status");
        String priority = req.getParameter("priority");
        String search = req.getParameter("search");
        int page = getPageParameter(req, "page", DEFAULT_PAGE);

        String selfUrl = req.getContextPath() + "/technician/tasks";
        String qs = req.getQueryString();
        if (qs != null && !qs.isBlank()) selfUrl += "?" + qs;
        req.setAttribute("returnTo", selfUrl);


        final int GRACE_MINUTES = 10;
        technicianService.autoCancelExpiredAssigned(GRACE_MINUTES);

        PaginationResponse<TaskAssignment> tasks = technicianService.getAllTasksWithFilter(
                technician.getEmployeeId(),
                status,
                priority,
                search,
                page,
                DEFAULT_PAGE_SIZE
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (TaskAssignment task : tasks.getData()) {
            if (task.getAssignedDate() != null) {
                task.setAssignedDateFormatted(task.getAssignedDate().format(formatter));
            }
        }

        ServiceResult statsResult = technicianService.getAllTasksStatistics(
                technician.getEmployeeId()
        );

        TaskStatistics taskStats = statsResult.getData(TaskStatistics.class);
        req.setAttribute("technician", technician);
        req.setAttribute("tasks", tasks);
        req.setAttribute("taskStats", taskStats);

        req.setAttribute("status", status);
        req.setAttribute("priority", priority);
        req.setAttribute("search", search);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", DEFAULT_PAGE_SIZE);


        req.getRequestDispatcher("/view/technician/tasks.jsp").forward(req, resp);
    }

    private int getPageParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String pageStr = req.getParameter(paramName);
        if(pageStr != null) {
            try {
                int page = Integer.parseInt(pageStr);
                return page > 0 ? page : defaultValue;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}