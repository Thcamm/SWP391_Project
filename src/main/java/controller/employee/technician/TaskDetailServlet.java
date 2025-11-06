package controller.employee.technician;

import common.constant.MessageConstants;
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
import service.employee.TechnicianService;
import service.vehicle.VehicleDiagnosticService;

import java.io.IOException;

@WebServlet("/technician/task-detail")
public class TaskDetailServlet extends HttpServlet {
    private final VehicleDiagnosticService diagnosticService = new VehicleDiagnosticService();
    private final TechnicianService technicianService = new TechnicianService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        String assignmentIdStr = req.getParameter("assignmentId");
        if(assignmentIdStr == null || assignmentIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        int assignmentId;
        try {
            assignmentId = Integer.parseInt(assignmentIdStr);
        } catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        ServiceResult me = technicianService.getTechnicianByUserId(userId);
        if(me.isError()){
            MessageHelper.setErrorMessage(session,me.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        Employee tech = me.getData(Employee.class);

        ServiceResult taskRs = technicianService.getTaskById(tech.getEmployeeId(), assignmentId);
        if(taskRs.isError()){
            MessageHelper.setErrorMessage(session, taskRs.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        TaskAssignment task = taskRs.getData(TaskAssignment.class);

        int page = 1, size = 10;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (Exception ignored) {}
        try {
            size = Integer.parseInt(req.getParameter("size"));
        } catch (Exception ignored) {}

        ServiceResult rs = diagnosticService.getDiagnosticsWithPartsPaged(assignmentId, page,size);
        if(rs.isError()){
            MessageHelper.setErrorMessage(session, rs.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        VehicleDiagnosticService.DiagnosticPageView vm = rs.getData(VehicleDiagnosticService.DiagnosticPageView.class);

        // Bắt returnTo từ request
        String returnTo = req.getParameter("returnTo");
        if(returnTo == null || returnTo.trim().isEmpty()) {
            returnTo = req.getContextPath() + "/technician/tasks";
        }

        req.setAttribute("task", task);
        req.setAttribute("vm", vm);
        req.setAttribute("returnTo", returnTo);
        req.getRequestDispatcher("/view/technician/task-detail.jsp").forward(req, resp);
    }


}
