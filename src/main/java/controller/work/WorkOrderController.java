package controller.work;

import model.employee.techmanager.TechManager;
import model.servicetype.ServiceRequest;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import service.employee.TechManagerService;
import service.work.WorkOrderService;
import service.work.WorkOrderDetailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/techmanager/workorders/*")
public class WorkOrderController extends HttpServlet {
    private WorkOrderService workOrderService;
    private WorkOrderDetailService workOrderDetailService;
    private TechManagerService techManagerService;

    @Override
    public void init() throws ServletException {
        this.workOrderService = new service.work.WorkOrderService();
        this.workOrderDetailService = new service.work.WorkOrderDetailService();
        this.techManagerService = new service.employee.TechManagerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/list");
            return;
        }

        try {
            switch (pathInfo) {
                case "/list":
                    showWorkOrders(request, response);
                    break;
                case "/view":
                    showWorkOrder(request, response);
                    break;
                case "/create":
                    showCreateWorkOrderForm(request, response);
                    break;
                case "/details":
                    showWorkOrderDetails(request, response);
                    break;
                case "/add-detail":
                    showAddWorkOrderDetailForm(request, response);
                    break;
                case "/edit-detail":
                    showEditWorkOrderDetailForm(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            switch (pathInfo) {
                case "/create":
                    createWorkOrder(request, response);
                    break;
                case "/add-detail":
                    addWorkOrderDetail(request, response);
                    break;
                case "/update-detail":
                    updateWorkOrderDetail(request, response);
                    break;
                case "/delete-detail":
                    deleteWorkOrderDetail(request, response);
                    break;
                case "/approve-detail":
                    approveWorkOrderDetail(request, response);
                    break;
                case "/decline-detail":
                    declineWorkOrderDetail(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void showWorkOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TechManager techManager = getCurrentTechManager(request);
        if (techManager == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<WorkOrder> workOrders = techManagerService.getWorkOrdersForUser(techManager);
            request.setAttribute("workOrders", workOrders);
            request.getRequestDispatcher("/view/workorders/list.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading work orders", e);
        }
    }

    private void showWorkOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String workOrderIdParam = request.getParameter("id");
        if (workOrderIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "WorkOrder ID is required");
            return;
        }

        try {
            int workOrderId = Integer.parseInt(workOrderIdParam);
            TechManager currentUser = getCurrentTechManager(request);

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
            List<WorkOrderDetail> workOrderDetails = workOrderDetailService.getWorkOrderDetailsByWorkOrder(workOrderId);

            if (workOrder == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "WorkOrder not found");
                return;
            }

            request.setAttribute("workOrder", workOrder);
            request.setAttribute("workOrderDetails", workOrderDetails);
            request.getRequestDispatcher("/view/workorders/details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid WorkOrder ID");
        } catch (Exception e) {
            throw new ServletException("Error loading work order", e);
        }
    }

    private void showCreateWorkOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/workorders/create.jsp").forward(request, response);
    }

    private void createWorkOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        TechManager techManager = getCurrentTechManager(request);
        if (techManager == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            BigDecimal estimateAmount = new BigDecimal(request.getParameter("estimateAmount"));

            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setRequestID(requestId);
            serviceRequest.setStatus("APPROVED");

            WorkOrder workOrder = workOrderService.createWorkOrderFromServiceRequest(techManager, serviceRequest,
                    estimateAmount);

            if (workOrder != null) {
                response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                        + workOrder.getWorkOrderId() + "&success=created");
            } else {
                response.sendRedirect(request.getContextPath() + "/techmanager/workorders/create?error=create_failed");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/create?error=" + e.getMessage());
        }
    }

    private void showWorkOrderDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String workOrderIdParam = request.getParameter("workOrderId");
        if (workOrderIdParam == null || workOrderIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "WorkOrder ID is required");
            return;
        }

        try {
            int workOrderId = Integer.parseInt(workOrderIdParam);
            TechManager currentUser = getCurrentTechManager(request);

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);

            if (workOrder == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "WorkOrder not found");
                return;
            }

            List<WorkOrderDetail> workOrderDetails = workOrderDetailService.getWorkOrderDetailsByWorkOrder(workOrderId);

            request.setAttribute("workOrder", workOrder);
            request.setAttribute("workOrderDetails", workOrderDetails);
            request.getRequestDispatcher("/view/workorders/details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid WorkOrder ID");
        } catch (Exception e) {
            throw new ServletException("Error loading work order details", e);
        }
    }

    private void showAddWorkOrderDetailForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String workOrderIdParam = request.getParameter("workOrderId");
        if (workOrderIdParam == null || workOrderIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "WorkOrder ID is required");
            return;
        }

        try {
            int workOrderId = Integer.parseInt(workOrderIdParam);
            TechManager currentUser = getCurrentTechManager(request);

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
            if (workOrder == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "WorkOrder not found");
                return;
            }

            request.setAttribute("workOrder", workOrder);
            request.getRequestDispatcher("/view/workorders/add-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid WorkOrder ID");
        } catch (Exception e) {
            throw new ServletException("Error loading add detail form", e);
        }
    }

    private void showEditWorkOrderDetailForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String workOrderIdParam = request.getParameter("workOrderId");
        String detailIdParam = request.getParameter("detailId");

        if (workOrderIdParam == null || detailIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "WorkOrder ID and Detail ID are required");
            return;
        }

        try {
            int workOrderId = Integer.parseInt(workOrderIdParam);
            int detailId = Integer.parseInt(detailIdParam);

            TechManager currentUser = getCurrentTechManager(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrderDetail workOrderDetail = workOrderDetailService.getWorkOrderDetailById(detailId);

            if (workOrderDetail == null || workOrderDetail.getWorkOrderId() != workOrderId) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "WorkOrder detail not found");
                return;
            }

            request.setAttribute("workOrderDetail", workOrderDetail);
            request.getRequestDispatcher("/view/workorders/edit-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID parameters");
        } catch (Exception e) {
            throw new ServletException("Error loading edit form", e);
        }
    }

    private void addWorkOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int workOrderId = Integer.parseInt(request.getParameter("workOrderId"));
            String source = request.getParameter("source");
            String taskDescription = request.getParameter("taskDescription");
            BigDecimal estimateHours = new BigDecimal(request.getParameter("estimateHours"));
            BigDecimal estimateAmount = new BigDecimal(request.getParameter("estimateAmount"));

            TechManager currentUser = getCurrentTechManager(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrderDetail detail = workOrderDetailService.createWorkOrderDetail(workOrderId,
                    WorkOrderDetail.Source.valueOf(source), taskDescription, estimateHours, estimateAmount);

            if (detail != null) {
                response.sendRedirect(
                        request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                                + "&success=detail_added");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                                + "&error=Failed to add detail");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                    + request.getParameter("workOrderId") + "&error=" + e.getMessage());
        }
    }

    private void updateWorkOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int workOrderId = Integer.parseInt(request.getParameter("workOrderId"));
            int detailId = Integer.parseInt(request.getParameter("detailId"));
            String source = request.getParameter("source");
            String taskDescription = request.getParameter("taskDescription");
            BigDecimal estimateHours = new BigDecimal(request.getParameter("estimateHours"));
            BigDecimal estimateAmount = new BigDecimal(request.getParameter("estimateAmount"));
            String approvalStatus = request.getParameter("approvalStatus");

            TechManager currentUser = getCurrentTechManager(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            WorkOrderDetail detail = workOrderDetailService.getWorkOrderDetailById(detailId);
            if (detail == null) {
                response.sendRedirect(
                        request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                                + "&error=Detail not found");
                return;
            }

            detail.setSource(WorkOrderDetail.Source.valueOf(source));
            detail.setTaskDescription(taskDescription);
            detail.setEstimateHours(estimateHours);
            detail.setEstimateAmount(estimateAmount);
            detail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.valueOf(approvalStatus));

            workOrderDetailService.updateWorkOrderDetail(detail);

            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                    + "&success=detail_updated");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                    + request.getParameter("workOrderId") + "&error=" + e.getMessage());
        }
    }

    private void deleteWorkOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int workOrderId = Integer.parseInt(request.getParameter("workOrderId"));
            int detailId = Integer.parseInt(request.getParameter("detailId"));

            TechManager currentUser = getCurrentTechManager(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(currentUser, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            workOrderDetailService.deleteWorkOrderDetail(detailId);

            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                    + "&success=detail_deleted");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                    + request.getParameter("workOrderId") + "&error=" + e.getMessage());
        }
    }

    private void approveWorkOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int workOrderId = Integer.parseInt(request.getParameter("workOrderId"));
            int detailId = Integer.parseInt(request.getParameter("detailId"));
            TechManager techManager = getCurrentTechManager(request);

            if (techManager == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(techManager, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            workOrderDetailService.approveWorkOrderDetail(detailId, techManager.getUserId());

            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                    + "&success=detail_approved");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                    + request.getParameter("workOrderId") + "&error=" + e.getMessage());
        }
    }

    private void declineWorkOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int workOrderId = Integer.parseInt(request.getParameter("workOrderId"));
            int detailId = Integer.parseInt(request.getParameter("detailId"));
            TechManager techManager = getCurrentTechManager(request);

            if (techManager == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!techManagerService.canAccessWorkOrder(techManager, workOrderId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this work order");
                return;
            }

            workOrderDetailService.declineWorkOrderDetail(detailId, techManager.getUserId());

            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId=" + workOrderId
                    + "&success=detail_declined");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/techmanager/workorders/details?workOrderId="
                    + request.getParameter("workOrderId") + "&error=" + e.getMessage());
        }
    }

    private TechManager getCurrentTechManager(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer userId = (Integer) session.getAttribute("userId");

            if (userId != null && userId > 0) {
                try {
                    return techManagerService.getTechManagerByUserId(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/view/error.jsp").forward(request, response);
    }
}