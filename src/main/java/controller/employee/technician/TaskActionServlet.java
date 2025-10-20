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

    public TaskActionServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);

        if (techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);

        String action = req.getParameter("action");
        String assignmentIdStr = req.getParameter("assignmentId");

        if(action == null || assignmentIdStr == null) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003); //invalid input
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);

            ServiceResult result ;
            switch (action.toLowerCase()) {
                case "accept" :
                    result = technicianService.acceptTask(technician.getEmployeeId(), assignmentId);
                    break;

                case "reject" :
                    result = technicianService.rejectTask(technician.getEmployeeId(), assignmentId);
                    break;
                default :
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    resp.sendRedirect(req.getContextPath() + "/technician/home");
                    return;
            }
            if(result.isSuccess()) {
                MessageHelper.setSuccessMessage(session, result.getMessage());
            }else {
                MessageHelper.setErrorMessage(session, result.getMessage());
            }
        }catch (NumberFormatException e){
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
        }

        resp.sendRedirect(req.getContextPath() + "/technician/home");

    }
}
