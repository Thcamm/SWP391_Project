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

@WebServlet("/technician/tasks-action")
public class TaskActionServlet extends HttpServlet {

    private final TechnicianService technicianService = new TechnicianService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Lấy session an toàn
        HttpSession session = req.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");

        // Lấy technician
        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);
        if (techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }
        Employee technician = techResult.getData(Employee.class);

        // Params
        String action = req.getParameter("action");             // accept | reject | start
        String assignmentIdStr = req.getParameter("assignmentId");
        String returnTo = req.getParameter("returnTo");         // optional: cho phép quay lại đúng trang lọc
        if (returnTo == null || returnTo.isBlank()) {
            // fallback: dùng Referer nếu có
            String referer = req.getHeader("Referer");
            if (referer != null && !referer.isBlank()) {
                returnTo = referer;
            } else {
                returnTo = req.getContextPath() + "/technician/tasks";
            }
        }

        if (action == null || assignmentIdStr == null) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(returnTo);
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);


            String normalized = action.trim().toLowerCase();
            if ("start".equals(normalized)) normalized = "accept";

            ServiceResult result;
            switch (normalized) {
                case "accept":
                    result = technicianService.acceptTask(technician.getEmployeeId(), assignmentId);
                    break;
                case "reject":
                    result = technicianService.rejectTask(technician.getEmployeeId(), assignmentId);
                    break;
                default:
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    resp.sendRedirect(returnTo);
                    return;
            }

            if (result.isSuccess()) {
                MessageHelper.setSuccessMessage(session, result.getMessage());
            } else {
                MessageHelper.setErrorMessage(session, result.getMessage());
            }

        } catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
        } catch (Exception e) {
            e.printStackTrace();
            MessageHelper.setErrorMessage(session, MessageConstants.ERR001);
        }


        resp.sendRedirect(returnTo);
    }
}
