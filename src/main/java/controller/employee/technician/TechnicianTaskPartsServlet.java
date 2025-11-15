package controller.employee.technician;

import common.message.ServiceResult;
import common.utils.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.employee.technician.TaskPartsVM;
import service.inventory.PartRequestService;

import java.io.IOException;

@WebServlet("/technician/task-parts")
public class TechnicianTaskPartsServlet extends HttpServlet {

    private final PartRequestService partService = new PartRequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        String partSearch = req.getParameter("partSearch");
        ServiceResult result = partService.getPartsForAssignment(assignmentId, partSearch);

        if (result.isError()) {
            MessageHelper.setErrorMessage(req.getSession(), result.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/tasks");
            return;
        }

        TaskPartsVM vm = result.getData(TaskPartsVM.class);
        req.setAttribute("vm", vm);

        req.getRequestDispatcher("/view/technician/task-parts.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        int partDetailId = Integer.parseInt(req.getParameter("partDetailId"));
        int qty          = Integer.parseInt(req.getParameter("quantity"));

        ServiceResult result = partService.createPartRequest(assignmentId, partDetailId, qty);

        MessageHelper.setSuccessMessage(req.getSession(), result.getMessage());
        resp.sendRedirect(req.getContextPath() + "/technician/task-parts?assignmentId=" + assignmentId);
    }

}
