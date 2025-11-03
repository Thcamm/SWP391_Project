package controller.employee.technician;

import common.DbContext;
import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.MessageHelper;
import dao.vehicle.VehicleDiagnosticDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.employee.Employee;
import model.inventory.DiagnosticPart;
import model.inventory.PartDetail;
import model.pagination.PaginationResponse;
import service.employee.TechnicianService;
import service.inventory.PartService;
import service.vehicle.VehicleDiagnosticService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/technician/diagnostic/edit")
public class EditDiagnosticServlet extends HttpServlet {
    private final VehicleDiagnosticService service = new VehicleDiagnosticService();
    private final TechnicianService techService = new TechnicianService();
    private final PartService partService = new PartService();
    private final VehicleDiagnosticDAO diagDAO = new VehicleDiagnosticDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession ss = req.getSession(false);
        Integer userId = (Integer) ss.getAttribute("userId");

        int diagnosticId;
        try { diagnosticId = Integer.parseInt(req.getParameter("diagnosticId")); }
        catch (Exception e) {
            MessageHelper.setErrorMessage(ss, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath()+"/technician/home");
            return;
        }

        ServiceResult me = techService.getTechnicianByUserId(userId);
        if (me.isError()) { MessageHelper.setErrorMessage(ss, me.getMessage()); resp.sendRedirect(req.getContextPath()+"/technician/home"); return; }
        Employee tech = me.getData(Employee.class);

        ServiceResult rs = service.loadForEdit(tech.getEmployeeId(), diagnosticId);
        if (rs.isError()) {
            MessageHelper.setErrorMessage(ss, rs.getMessage());
            resp.sendRedirect(req.getContextPath()+"/technician/home");
            return;
        }

        VehicleDiagnosticService.EditDiagnosticVM vm = rs.getData(VehicleDiagnosticService.EditDiagnosticVM.class);
        req.setAttribute("vm", vm);

        //cung cap danh sach part de render select (kem phan trng nhu man create)
        String partQuery = req.getParameter("partQuery");
        int page = 1, size = 20;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}

        ServiceResult parts = partService.searchAvailableParts(partQuery, page, size);
        PaginationResponse<PartDetail> partsPage = parts.getData(PaginationResponse.class);

        req.setAttribute("availableParts", partsPage.getData());
        req.setAttribute("partQuery", partQuery);
        req.setAttribute("partsPage", partsPage.getCurrentPage());
        req.setAttribute("partsTotalPages", partsPage.getTotalPages());
        req.setAttribute("partsTotalItems", partsPage.getTotalItems());
        req.setAttribute("partsPageSize", partsPage.getItemsPerPage());

        req.getRequestDispatcher("/view/technician/edit-diagnostic.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession ss = req.getSession(false);
        Integer userId = (Integer) ss.getAttribute("userId");

        int diagnosticId;
        try {
            diagnosticId = Integer.parseInt(req.getParameter("diagnosticId"));
        } catch (Exception e) {
            MessageHelper.setErrorMessage(ss, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath()+"/technician/home");
            return;
        }

        ServiceResult me = techService.getTechnicianByUserId(userId);
        if (me.isError()) { MessageHelper.setErrorMessage(ss, me.getMessage()); resp.sendRedirect(req.getContextPath()+"/technician/home"); return; }
        Employee tech = me.getData(Employee.class);

        String issueFound = req.getParameter("issueFound");
        String laborCostStr = req.getParameter("laborCost");
        BigDecimal labor = BigDecimal.ZERO;
        try {
            if (laborCostStr != null && !laborCostStr.isBlank())
                labor = new BigDecimal(laborCostStr);
        } catch (NumberFormatException ignored) {}

        String[] partIds   = req.getParameterValues("partDetailId[]");
        String[] qtys      = req.getParameterValues("quantity[]");
        String[] conds     = req.getParameterValues("condition[]");
        String[] reasons   = req.getParameterValues("reason[]");

        List<DiagnosticPart> newParts = new ArrayList<>();

        if (partIds != null) {
            for (int i = 0; i < partIds.length; i++) {
                if (partIds[i] == null || partIds[i].isBlank()) continue;
                try {
                    int pid = Integer.parseInt(partIds[i]);
                    int q   = Integer.parseInt(qtys[i]);
                    DiagnosticPart dp = new DiagnosticPart();
                    dp.setPartDetailID(pid);
                    dp.setQuantityNeeded(q);
                    dp.setPartCondition(conds != null && i < conds.length ? conds[i] : "RECOMMENDED");
                    dp.setReasonForReplacement(reasons != null && i < reasons.length ? reasons[i] : null);
                    newParts.add(dp);
                } catch (Exception ignored) {}
            }
        }

        ServiceResult rs = service.updateDiagnosticDraf(tech.getEmployeeId(), diagnosticId, issueFound, labor, newParts);
        if(rs.isSuccess()) {
            Integer assignmentId ;
            try (Connection c = DbContext.getConnection()){
                assignmentId = new VehicleDiagnosticDAO().getAssignmentIdByDiagnostic(c, diagnosticId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            MessageHelper.setSuccessMessage(ss, rs.getMessage());

            if (assignmentId != null) {
                resp.sendRedirect(req.getContextPath()+"/technician/task-detail?assignmentId="+assignmentId);
            } else {
                resp.sendRedirect(req.getContextPath()+"/technician/home");
            }


        }else {
            MessageHelper.setErrorMessage(ss, rs.getMessage());
            resp.sendRedirect(req.getContextPath()+"/technician/diagnostic/edit?diagnosticId="+diagnosticId);
        }
    }
}
