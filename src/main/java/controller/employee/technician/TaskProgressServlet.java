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

    public TaskProgressServlet() { super(); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            MessageHelper.setErrorMessage(session, MessageConstants.AUTH001);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);
        if (techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);


        String assignmentIdStr = req.getParameter("assignmentId");
        String action = req.getParameter("action");
        String progressStr = req.getParameter("progressPercentage");
        String notes = req.getParameter("notes");
        String returnTo = req.getParameter("returnTo");

        if (assignmentIdStr == null || action == null) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);
            ServiceResult result;

            if ("complete".equalsIgnoreCase(action)) {
                result = technicianService.completeTask(
                        technician.getEmployeeId(), assignmentId, notes
                );

            } else if ("update".equalsIgnoreCase(action)) {
                if (progressStr == null) {
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
                    return;
                }

                int progressPercentage;
                try {
                    progressPercentage = Integer.parseInt(progressStr.trim());
                } catch (NumberFormatException ex) {
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
                    return;
                }

                // Validate 0..100
                if (progressPercentage < 0 || progressPercentage > 100) {
                    MessageHelper.setErrorMessage(session, "Progress must be between 0 and 100.");
                    redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
                    return;
                }

                result = technicianService.updateProgress(
                        technician.getEmployeeId(), assignmentId, progressPercentage, notes
                );

            } else {
                MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
                return;
            }

            if (result.isSuccess()) {
                MessageHelper.setSuccessMessage(session, result.getMessage());
            } else {
                MessageHelper.setErrorMessage(session, result.getMessage());
            }

        } catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
        }


        redirectBack(req, resp, returnTo, req.getContextPath() + "/technician/home");
    }

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp,
                              String returnTo, String fallback) throws IOException {
        if (returnTo != null && !returnTo.isBlank()) {
            resp.sendRedirect(returnTo);
        } else {
            resp.sendRedirect(fallback);
        }
    }
}
