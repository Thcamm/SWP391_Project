package controller.employee.technician;

import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.MessageHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import service.employee.TechnicianService;

@WebServlet("/technician/update-progress-form")
public class UpdateProgressFormServlet extends HttpServlet {

    private final TechnicianService technicianService = new TechnicianService();

    public UpdateProgressFormServlet() {
        super();
    }

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);

        if(techResult.isError()){
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);

        String assignmentIdStr = req.getParameter("assignmentId");
        if(assignmentIdStr == null || assignmentIdStr.trim().isEmpty()) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "technician/home");
            return;
        }

        try  {
            int assignmentId = Integer.parseInt(assignmentIdStr);
            ServiceResult taskResult = technicianService.getTaskById(technician.getEmployeeId(), assignmentId);

            if(taskResult.isError()){
                MessageHelper.setErrorMessage(session, taskResult.getMessage());
                resp.sendRedirect(req.getContextPath() + "technician/home");
                return;
            }

            TaskAssignment task = taskResult.getData(TaskAssignment.class);

            if(!"IN_PROGRESS".equals(task.getStatus())) {
                MessageHelper.setErrorMessage(session, MessageConstants.TASK010); // Task not in progress
                resp.sendRedirect(req.getContextPath() + "technician/home");
                return;
            }

            req.setAttribute("task", task);
            req.setAttribute("technician", technician);

            req.getRequestDispatcher("/view/technician/update-progress.jsp").forward(req, resp);
        }catch (NumberFormatException e){
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "technician/home");
            return;
        }

    }

    @Override
    protected void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        super.doPost(req, resp);
    }
}
