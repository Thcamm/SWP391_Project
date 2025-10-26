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
import service.employee.TechnicianService;

import java.io.IOException;

@WebServlet("/technician/update-progress")
public class TaskProgressServlet extends HttpServlet {

    private final TechnicianService technicianService = new TechnicianService();

    public TaskProgressServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");
        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);

        if(techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);

        String assigmentIdStr = req.getParameter("assignmentId");
        String action = req.getParameter("action");
        String progressStr = req.getParameter("progressPercentage");
        String notes = req.getParameter("notes");

        if(assigmentIdStr == null || action == null) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assigmentIdStr);
            ServiceResult result ;

            if("complete".equals(action)) {
                result = technicianService.completeTask(
                        technician.getEmployeeId(),
                        assignmentId,
                        notes
                );

            }else if ("update".equals(action)) {
                if(progressStr == null) {
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    resp.sendRedirect(req.getContextPath() + "/technician/home");
                    return;
                }

                int progressPercentage = Integer.parseInt(progressStr);
                result = technicianService.updateProgress(
                        technician.getEmployeeId(),
                        assignmentId,
                        progressPercentage,
                        notes

                );


            }else {
                MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                resp.sendRedirect(req.getContextPath() + "/technician/home");
                return;
            }

            if(result.isSuccess()) {
                MessageHelper.setSuccessMessage(session, result.getMessage());

            }else {
                MessageHelper.setErrorMessage(session, result.getMessage());
            }
        }catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);

        }

        resp.sendRedirect(req.getContextPath() + "/technician/home");

    }
}
