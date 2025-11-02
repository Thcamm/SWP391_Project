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
import model.inventory.DiagnosticPart;
import model.inventory.PartDetail;
import model.vehicle.VehicleDiagnostic;
import service.employee.TechnicianDiagnosticService;
import service.employee.TechnicianService;
import service.inventory.PartService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/technician/create-diagnostic")
public class CreateDiagnosticServlet extends HttpServlet {
    private final TechnicianService technicianService = new TechnicianService();
    private final TechnicianDiagnosticService diagnosticService = new TechnicianDiagnosticService();
    private final PartService partService = new PartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);
        if (techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }
        Employee technician = techResult.getData(Employee.class);

        String assignmentIdStr = req.getParameter("assignmentId");
        if (assignmentIdStr == null || assignmentIdStr.trim().isEmpty()) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);

            ServiceResult taskResult = technicianService.getTaskById(technician.getEmployeeId(), assignmentId);
            if (taskResult.isError()) {
                MessageHelper.setErrorMessage(session, taskResult.getMessage());
                resp.sendRedirect(req.getContextPath() + "/technician/home");
                return;
            }
            TaskAssignment task = taskResult.getData(TaskAssignment.class);

            if (task.getTaskType() != TaskAssignment.TaskType.DIAGNOSIS ||
                    task.getStatus()   != TaskAssignment.TaskStatus.IN_PROGRESS) {
                MessageHelper.setErrorMessage(session, MessageConstants.TASK010);
                resp.sendRedirect(req.getContextPath() + "/technician/home");
                return;
            }

            // === NEW: đọc tham số tìm kiếm & phân trang
            String partQuery = req.getParameter("partQuery");
            int page = 1, size = 20;
            try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
            try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}

            // Gọi service mới (trả về PaginationResponse<PartDetail>)
            ServiceResult partsResult = partService.searchAvailableParts(partQuery, page, size);
            model.pagination.PaginationResponse<model.inventory.PartDetail> pageData =
                    partsResult.getData(model.pagination.PaginationResponse.class);

            req.setAttribute("task", task);
            req.setAttribute("technician", technician);

            // list + meta phân trang
            req.setAttribute("availableParts", pageData.getData());
            req.setAttribute("partQuery", partQuery);
            req.setAttribute("partsPage", pageData.getCurrentPage());
            req.setAttribute("partsTotalPages", pageData.getTotalPages());
            req.setAttribute("partsTotalItems", pageData.getTotalItems());
            req.setAttribute("partsPageSize", pageData.getItemsPerPage());
            req.setAttribute("selectedPartMap", java.util.Collections.emptyMap());

            req.getRequestDispatcher("/view/technician/create-diagnostic.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");

        ServiceResult techResult = technicianService.getTechnicianByUserId(userId);
        if (techResult.isError()) {
            MessageHelper.setErrorMessage(session, techResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }

        Employee technician = techResult.getData(Employee.class);

        String assignmentIdStr = req.getParameter("assignmentId");
        String action = req.getParameter("action");


        // Parts arrays (cần để forward lại JSP giữ dữ liệu người dùng)
        String[] partDetailIds = req.getParameterValues("partDetailId[]");
        String[] quantities    = req.getParameterValues("quantity[]");
        String[] conditions    = req.getParameterValues("condition[]");
        String[] reasons       = req.getParameterValues("reason[]");

        if (assignmentIdStr == null || assignmentIdStr.trim().isEmpty()) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
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

        // Lấy task để hiển thị header khi forward lại
        ServiceResult taskResult = technicianService.getTaskById(technician.getEmployeeId(), assignmentId);
        if (taskResult.isError()) {
            MessageHelper.setErrorMessage(session, taskResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }
        TaskAssignment task = taskResult.getData(TaskAssignment.class);

        if ("filterParts".equals(action) || "clearFilter".equals(action)
                || "addPart".equals(action) || (action != null && action.startsWith("removePart"))) {

            String partQuery = req.getParameter("partQuery");
            int page = 1, size = 20;
            try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
            try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}

            if ("clearFilter".equals(action)) {
                partQuery = "";
                page = 1;
            }

            ServiceResult partsResult = partService.searchAvailableParts(partQuery, page, size);
            model.pagination.PaginationResponse<model.inventory.PartDetail> pageData =
                    partsResult.getData(model.pagination.PaginationResponse.class);

            req.setAttribute("task", task);
            req.setAttribute("technician", technician);
            req.setAttribute("availableParts", pageData.getData());
            req.setAttribute("partQuery", partQuery);
            req.setAttribute("partsPage", pageData.getCurrentPage());
            req.setAttribute("partsTotalPages", pageData.getTotalPages());
            req.setAttribute("partsTotalItems", pageData.getTotalItems());
            req.setAttribute("partsPageSize", pageData.getItemsPerPage());

            if (action != null && action.startsWith("removePart")) {
                try {
                    int idx = Integer.parseInt(action.replace("removePart", ""));
                    req.setAttribute("removeIndex", idx);
                } catch (NumberFormatException ignored) {}
            }

            Map<Integer, PartDetail> selectedPartMap = new HashMap<>();
            if (partDetailIds != null) {
                for (String s : partDetailIds) {
                    if (s == null || s.isBlank()) continue;
                    try {
                        int pid = Integer.parseInt(s);
                        ServiceResult pr = partService.getPartDetailById(pid);
                        if (pr.isSuccess() && pr.getData() != null) {
                            selectedPartMap.put(pid, pr.getData(PartDetail.class));
                        }
                    } catch (NumberFormatException ignored) { }
                }
            }
            req.setAttribute("selectedPartMap", selectedPartMap);

            // bam add another part -> render 1 hang trong
            if ("addPart".equals(action)) {
                req.setAttribute("appendEmptyRow", true);
            }
            req.getRequestDispatcher("/view/technician/create-diagnostic.jsp").forward(req, resp);
            return;
        }

        //  nhanh submit that
        String issueFound   = req.getParameter("issueFound");
        String laborCostStr = req.getParameter("laborCost");

        // BASIC VALIDATION
        if (issueFound == null || issueFound.trim().isEmpty()) {
            MessageHelper.setErrorMessage(session, MessageConstants.VAL001);
            resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
            return;
        }

        // Validate issue description length
        if (issueFound.trim().length() < 20) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
            return;
        }

        try {
            //  PARSE LABOR COST
            BigDecimal laborCost = BigDecimal.ZERO;
            if (laborCostStr != null && !laborCostStr.trim().isEmpty()) {
                try {
                    laborCost = new BigDecimal(laborCostStr);
                    if (laborCost.compareTo(BigDecimal.ZERO) < 0) {
                        MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                        resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                    resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                    return;
                }
            }

            // CREATE VEHICLE DIAGNOSTIC OBJECT
            VehicleDiagnostic diagnostic = new VehicleDiagnostic();
            diagnostic.setAssignmentID(assignmentId);
            diagnostic.setIssueFound(issueFound.trim());
            diagnostic.setEstimateCost(laborCost);
            diagnostic.setStatus(true);

            //  PARSE PARTS
            List<DiagnosticPart> parts = new ArrayList<>();

            if (partDetailIds != null && partDetailIds.length > 0) {
                for (int i = 0; i < partDetailIds.length; i++) {
                    if (partDetailIds[i] != null && !partDetailIds[i].trim().isEmpty()) {
                        try {
                            int partDetailId = Integer.parseInt(partDetailIds[i]);
                            int qty = Integer.parseInt(quantities[i]);

                            if (qty <= 0) {
                                MessageHelper.setErrorMessage(session, MessageConstants.ERR003); // invalid input
                                resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                                return;
                            }

                            ServiceResult pr = partService.getPartDetailById(partDetailId);
                            if (pr.isError() || pr.getData() == null) {
                                MessageHelper.setErrorMessage(session, MessageConstants.ERR002); // Not found
                                resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                                return;
                            }

                            PartDetail pd = pr.getData(PartDetail.class);

                            if (pd.getQuantity() < qty) {
                                MessageHelper.setErrorMessage(
                                        session,
                                        String.format("Insufficient stock for %s (%s). Available: %d, Requested: %d",
                                                pd.getPartName(), pd.getSku(), pd.getQuantity(), qty)
                                );
                                resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                                return;
                            }




                            DiagnosticPart part = new DiagnosticPart();
                            part.setPartDetailID(Integer.parseInt(partDetailIds[i]));
                            part.setQuantityNeeded(Integer.parseInt(quantities[i]));
                            part.setUnitPrice(pd.getUnitPrice());
                            part.setPartCondition(conditions[i]);
                            part.setReasonForReplacement(reasons[i]);
                            part.setApproved(false);



                            parts.add(part);

                        } catch (NumberFormatException e) {
                            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
                            resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
                            return;
                        }
                    }
                }
            }

            diagnostic.setParts(parts);

            // CALL SERVICE TO CREATE DIAGNOSTIC
            ServiceResult result = diagnosticService.createDiagnosticWithParts(
                    technician.getEmployeeId(), diagnostic
            );

            // HANDLE RESULT
            if (result.isSuccess()) {
                req.setAttribute("task", task);               // để header vẫn có thông tin
                req.setAttribute("successMessage", result.getMessage());
                req.setAttribute("showSuccessInline", true);

                String partQuery = ""; int page = 1, size = 20;
                ServiceResult partsResult = partService.searchAvailableParts(partQuery, page, size);
                var pageData = partsResult.getData(model.pagination.PaginationResponse.class);
                req.setAttribute("availableParts", pageData.getData());
                req.setAttribute("partsPage", pageData.getCurrentPage());
                req.setAttribute("partsTotalPages", pageData.getTotalPages());
                req.setAttribute("partsTotalItems", pageData.getTotalItems());
                req.setAttribute("partsPageSize", pageData.getItemsPerPage());
                req.setAttribute("selectedPartMap", java.util.Collections.emptyMap());
                req.getRequestDispatcher("/view/technician/create-diagnostic.jsp").forward(req, resp);
                return;
            } else {
                MessageHelper.setErrorMessage(session, result.getMessage());
                resp.sendRedirect(req.getContextPath() + "/technician/create-diagnostic?assignmentId=" + assignmentId);
            }

        } catch (NumberFormatException e) {
            MessageHelper.setErrorMessage(session, MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
        } catch (Exception e) {
            e.printStackTrace();
            MessageHelper.setErrorMessage(session, MessageConstants.ERR001);
            resp.sendRedirect(req.getContextPath() + "/technician/home");
        }
    }
}
