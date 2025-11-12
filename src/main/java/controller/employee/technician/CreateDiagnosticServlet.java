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
import service.vehicle.VehicleDiagnosticService;
import util.MailService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Create Diagnostic for technician
 */
@WebServlet("/technician/create-diagnostic")
public class CreateDiagnosticServlet extends HttpServlet {
    private final TechnicianService technicianService = new TechnicianService();
    private final TechnicianDiagnosticService diagnosticService = new TechnicianDiagnosticService();
    private final PartService partService = new PartService();
    private final VehicleDiagnosticService vehicleDiagnosticService = new VehicleDiagnosticService();


    private int parseIntOr(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ignored) { return def; }
    }

    /** Nạp list parts để hiển thị + map các part đã chọn */
    private void loadPartsForDisplay(HttpServletRequest req, String partQuery, String[] partDetailIds) {
        int page = parseIntOr(req.getParameter("page"), 1);
        int size = parseIntOr(req.getParameter("size"), 20);

        ServiceResult partsResult = partService.searchAvailableParts(partQuery, page, size);
        var pageData = partsResult.getData(model.pagination.PaginationResponse.class);

        req.setAttribute("availableParts", pageData.getData());
        req.setAttribute("partQuery", partQuery);
        req.setAttribute("partsPage", pageData.getCurrentPage());
        req.setAttribute("partsTotalPages", pageData.getTotalPages());
        req.setAttribute("partsTotalItems", pageData.getTotalItems());
        req.setAttribute("partsPageSize", pageData.getItemsPerPage());


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
    }

    private void forwardForm(HttpServletRequest req, HttpServletResponse resp,
                             TaskAssignment task, Employee technician,
                             List<String> formErrors, String successMessage)
            throws ServletException, IOException {
        if (task != null) req.setAttribute("task", task);
        if (technician != null) req.setAttribute("technician", technician);
        if (formErrors != null && !formErrors.isEmpty()) {
            req.setAttribute("formErrors", formErrors);
        }
        if (successMessage != null) {
            req.setAttribute("successMessage", successMessage);
            req.setAttribute("showSuccessInline", true);
        }
        req.getRequestDispatcher("/view/technician/create-diagnostic.jsp").forward(req, resp);
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        vehicleDiagnosticService.autoRejectExpiredDiagnostics(10);
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

            if (task.getStatus() != TaskAssignment.TaskStatus.IN_PROGRESS) {
                MessageHelper.setErrorMessage(session, MessageConstants.TASK010);
                resp.sendRedirect(req.getContextPath() + "/technician/home");
                return;
            }

            loadPartsForDisplay(req, req.getParameter("partQuery"), null);
            req.setAttribute("task", task);
            req.setAttribute("technician", technician);
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

        // mảng parts từ form
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

        // lấy task (để render header/validate trạng thái)
        ServiceResult taskResult = technicianService.getTaskById(technician.getEmployeeId(), assignmentId);
        if (taskResult.isError()) {
            MessageHelper.setErrorMessage(session, taskResult.getMessage());
            resp.sendRedirect(req.getContextPath() + "/technician/home");
            return;
        }
        TaskAssignment task = taskResult.getData(TaskAssignment.class);

        // --- Các action phụ: search/clear/add/remove ---
        if ("filterParts".equals(action) || "clearFilter".equals(action)
                || "addPart".equals(action) || (action != null && action.startsWith("removePart"))) {

            String partQuery = "clearFilter".equals(action) ? "" : req.getParameter("partQuery");
            loadPartsForDisplay(req, partQuery, partDetailIds);

            if (action != null && action.startsWith("removePart")) {
                try {
                    int idx = Integer.parseInt(action.replace("removePart", ""));
                    req.setAttribute("removeIndex", idx);
                } catch (NumberFormatException ignored) {}
            }
            if ("addPart".equals(action)) {
                req.setAttribute("appendEmptyRow", true);
            }

            req.setAttribute("task", task);
            req.setAttribute("technician", technician);
            req.getRequestDispatcher("/view/technician/create-diagnostic.jsp").forward(req, resp);
            return;
        }

        // --------- Submit thật ---------
        String issueFound   = req.getParameter("issueFound");
        String laborCostStr = req.getParameter("laborCost");

        List<String> errors = new ArrayList<>();

        // validate issue
        if (issueFound == null || issueFound.trim().isEmpty()) {
            errors.add("Issue Found is required.");
        } else if (issueFound.trim().length() < 20) {
            errors.add("Issue description must be at least 20 characters.");
        }

        // labor cost
        BigDecimal laborCost = BigDecimal.ZERO;
        if (laborCostStr != null && !laborCostStr.trim().isEmpty()) {
            try {
                laborCost = new BigDecimal(laborCostStr.trim());
                if (laborCost.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Labor cost must be >= 0.");
                }
            } catch (NumberFormatException ex) {
                errors.add("Labor cost is not a valid number.");
            }
        }

        // parse & validate parts
        List<DiagnosticPart> parts = new ArrayList<>();
        if (partDetailIds != null && partDetailIds.length > 0) {
            for (int i = 0; i < partDetailIds.length; i++) {
                String idStr = partDetailIds[i];
                String qtyStr = (quantities != null && i < quantities.length) ? quantities[i] : null;

                // Bỏ qua dòng trống hoàn toàn
                if ((idStr == null || idStr.isBlank()) &&
                        (qtyStr == null || qtyStr.isBlank())) {
                    continue;
                }

                if (idStr == null || idStr.isBlank()) {
                    errors.add("Line " + (i + 1) + ": Part is required.");
                    continue;
                }
                int qty;
                int partDetailId;
                try {
                    partDetailId = Integer.parseInt(idStr);
                    qty = Integer.parseInt(qtyStr);
                } catch (NumberFormatException ex) {
                    errors.add("Line " + (i + 1) + ": Invalid number format.");
                    continue;
                }
                if (qty <= 0) {
                    errors.add("Line " + (i + 1) + ": Quantity must be ≥ 1.");
                    continue;
                }

                ServiceResult pr = partService.getPartDetailById(partDetailId);
                if (pr.isError() || pr.getData() == null) {
                    errors.add("Line " + (i + 1) + ": Part not found.");
                    continue;
                }
                PartDetail pd = pr.getData(PartDetail.class);
                if (pd.getQuantity() < qty) {
                    errors.add(String.format("Line %d: Insufficient stock for %s (%s). Available: %d, requested: %d",
                            (i + 1), pd.getPartName(), pd.getSku(), pd.getQuantity(), qty));
                    continue;
                }

                DiagnosticPart row = new DiagnosticPart();
                row.setPartDetailID(partDetailId);
                row.setQuantityNeeded(qty);
                row.setUnitPrice(pd.getUnitPrice());

                // CHANGED: parse PartCondition an toàn sang enum
                String condRaw = (conditions != null && i < conditions.length) ? conditions[i] : null;
                DiagnosticPart.PartCondition condEnum = parseConditionOrDefault(condRaw, DiagnosticPart.PartCondition.REQUIRED); // CHANGED
                row.setPartCondition(condEnum);

                row.setReasonForReplacement((reasons != null && i < reasons.length) ? reasons[i] : null);
                row.setApproved(false); // KH chưa duyệt
                parts.add(row);
            }
        }

        if (!errors.isEmpty()) {
            loadPartsForDisplay(req, req.getParameter("partQuery"), partDetailIds);
            forwardForm(req, resp, task, technician, errors, null);
            return;
        }

        // TÍNH TOTAL PARTS
        BigDecimal partsSum = BigDecimal.ZERO;
        for (DiagnosticPart p : parts) {
            BigDecimal price = p.getUnitPrice() == null ? BigDecimal.ZERO : p.getUnitPrice();
            BigDecimal line = price.multiply(BigDecimal.valueOf(p.getQuantityNeeded()));
            partsSum = partsSum.add(line);
        }
        // TÍNH TOTAL ESTIMATE = labor + parts
        BigDecimal totalEstimate = laborCost.add(partsSum);

        // build diagnostic object
        VehicleDiagnostic diagnostic = new VehicleDiagnostic();
        diagnostic.setAssignmentID(assignmentId);
        diagnostic.setIssueFound(issueFound.trim());
        diagnostic.setEstimateCost(totalEstimate);   // tổng (labor + parts)
        diagnostic.setLaborCostInput(laborCost);
        diagnostic.setParts(parts);

        // CHANGED: Status mặc định SUBMITTED (pending approval)
        diagnostic.setStatus(VehicleDiagnostic.DiagnosticStatus.SUBMITTED);

        // call service
        ServiceResult result = diagnosticService.createDiagnosticWithParts(
                technician.getEmployeeId(), diagnostic);

        if (result.isSuccess()) {
            System.out.println(task.getCustomerEmail());
            MailService.sendEmail(task.getCustomerEmail(), "Diagnostic Report Created — Please Review Within 10 Minutes",
                    "A diagnostic has been created for your vehicle." +
                            "Please review it and choose whether to Accept or Reject within 10 minutes." +
                            "If no action is taken within 10 minutes,the system will automatically reject this diagnostic.\n" +
                            "Garage Management System");
            loadPartsForDisplay(req, "", null);
            forwardForm(req, resp, task, technician, null, String.valueOf(result.getMessage()));
        } else {
            loadPartsForDisplay(req, req.getParameter("partQuery"), partDetailIds);
            req.setAttribute("errorMessage", result.getMessage());
            forwardForm(req, resp, task, technician, null, null);
        }
    }

    /** CHANGED: helper parse PartCondition từ string form */
    private DiagnosticPart.PartCondition parseConditionOrDefault(String raw, DiagnosticPart.PartCondition def) {
        if (raw == null || raw.isBlank()) return def;
        try {
            return DiagnosticPart.PartCondition.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return def;
        }
    }

}