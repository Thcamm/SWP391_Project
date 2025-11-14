package service.work;

import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import dao.inventory.WorkOrderPartDAO;
import dao.workorder.TaskAssignmentDAO;
import dao.customer.CustomerDAO;
import dao.vehicle.VehicleDAO;
import dao.user.UserDAO;
import dao.inventory.PartDAO;
import model.dto.CSWorkOrderDetailView;
import model.dto.CSWorkOrderDetailView.*;
import model.employee.technician.TaskAssignment;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import model.inventory.WorkOrderPart;
import model.customer.Customer;
import model.vehicle.Vehicle;
import model.user.User;
import model.inventory.Part;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling logic for displaying Work Order details
 */
public class CSWorkOrderDetailService {

    private final WorkOrderDAO workOrderDAO;
    private final WorkOrderDetailDAO detailDAO;
    private final WorkOrderPartDAO partDAO;
    private final TaskAssignmentDAO taskDAO;
    private final CustomerDAO customerDAO;
    private final VehicleDAO vehicleDAO;
    private final UserDAO userDAO;
    private final PartDAO partInfoDAO;

    public CSWorkOrderDetailService() {
        this.workOrderDAO = new WorkOrderDAO();
        this.detailDAO = new WorkOrderDetailDAO();
        this.partDAO = new WorkOrderPartDAO();
        this.taskDAO = new TaskAssignmentDAO();
        this.customerDAO = new CustomerDAO();
        this.vehicleDAO = new VehicleDAO();
        this.userDAO = new UserDAO();
        this.partInfoDAO = new PartDAO();
    }

    /**
     * Retrieve detailed information of a Work Order
     */
    public CSWorkOrderDetailView getWorkOrderDetailView(int workOrderId) throws Exception {
        // 1. Get Work Order information
        WorkOrder workOrder = workOrderDAO.getWorkOrderById(workOrderId);
        if (workOrder == null) {
            return null;
        }

        // 2. Create DTO
        CSWorkOrderDetailView view = new CSWorkOrderDetailView();
        view.setWorkOrder(workOrder);

        // 3. Get Customer and Vehicle information
        Customer customer = customerDAO.getCustomerByRequestId(workOrder.getRequestId());
        view.setCustomer(customer);

        if (customer != null) {
            Vehicle vehicle = vehicleDAO.getVehicleByRequestId(workOrder.getRequestId());
            view.setVehicle(vehicle);
        }

        // 4. Get list of Work Order Details
        List<WorkOrderDetail> details = detailDAO.getDetailsByWorkOrderId(workOrderId);
        List<WorkOrderDetailInfo> detailInfos = new ArrayList<>();

        int totalDetails = 0;
        int completedDetails = 0;
        int totalTasks = 0;
        int completedTasks = 0;
        BigDecimal totalEstimatedAmount = BigDecimal.ZERO;
        BigDecimal totalActualAmount = BigDecimal.ZERO;
        int pendingApprovals = 0;

        for (WorkOrderDetail detail : details) {
            WorkOrderDetailInfo info = new WorkOrderDetailInfo();
            info.setDetail(detail);

            // Source label
            info.setSourceLabel(detail.getSource().equals("REQUEST") ? "Customer Request" : "Diagnosis");

            // Status label and color
            String[] detailStatus = getDetailStatusInfo(detail.getSource().name());
            info.setStatusLabel(detailStatus[0]);
            info.setStatusColor(detailStatus[1]);

            // Approval status
            String[] approvalStatus = getApprovalStatusInfo(detail.getApprovalStatus().name());
            info.setApprovalStatusLabel(approvalStatus[0]);
            info.setApprovalStatusColor(approvalStatus[1]);

            // Approved by
            if (detail.getApprovedByUserId() != null) {
                User approver = userDAO.getUserById(detail.getApprovedByUserId());
                info.setApprovedByName(approver != null ? approver.getFullName() : "N/A");
            }

            // Get tasks of this detail
            List<TaskAssignment> tasks = taskDAO.getTasksByDetailId(detail.getDetailId());
            List<TaskAssignmentInfo> taskInfos = new ArrayList<>();

            for (TaskAssignment task : tasks) {
                TaskAssignmentInfo taskInfo = new TaskAssignmentInfo();
                taskInfo.setTask(task);

                // Technician name
                User tech = userDAO.getUserById(task.getAssignToTechID());
                taskInfo.setTechnicianName(tech != null ? tech.getFullName() : "N/A");

                // Task type
                taskInfo.setTaskTypeLabel(task.getTaskType().name());

                // Priority
                String[] priority = getPriorityInfo(task.getPriority().getCssClass());
                taskInfo.setPriorityLabel(priority[0]);
                taskInfo.setPriorityColor(priority[1]);

                // Status
                String[] status = getTaskStatusInfo(task.getStatus().name());
                taskInfo.setStatusLabel(status[0]);
                taskInfo.setStatusColor(status[1]);

                taskInfos.add(taskInfo);

                totalTasks++;
                if ("COMPLETE".equals(task.getStatus())) {
                    completedTasks++;
                }
            }

            info.setTasks(taskInfos);
            detailInfos.add(info);

            totalDetails++;
            if (detail.getDetailStatus() == WorkOrderDetail.DetailStatus.COMPLETE) {
                completedDetails++;
            }

            if (detail.getApprovalStatus() == WorkOrderDetail.ApprovalStatus.PENDING) {
                pendingApprovals++;
            }

            totalEstimatedAmount = totalEstimatedAmount.add(
                    detail.getEstimateAmount() != null ? detail.getEstimateAmount() : BigDecimal.ZERO);

        }

        view.setDetails(detailInfos);

        // 5. Get list of Parts
        List<WorkOrderPart> parts = partDAO.getPartsByWorkOrderId(workOrderId);
        List<PartUsageInfo> partInfos = new ArrayList<>();

        int totalParts = 0;
        int deliveredParts = 0;

        for (WorkOrderPart part : parts) {
            PartUsageInfo partInfo = new PartUsageInfo();
            partInfo.setPart(part);

            // Part name
            Part partDetail = partInfoDAO.getPartById(part.getPartDetailID());
            partInfo.setPartName(partDetail != null ? partDetail.getPartName() : "N/A");

            // Status
            String[] partStatus = getPartStatusInfo(part.getRequestStatus());
            partInfo.setStatusLabel(partStatus[0]);
            partInfo.setStatusColor(partStatus[1]);

            // Requested by
            User requester = userDAO.getUserById(part.getRequestByID());
            partInfo.setRequestedByName(requester != null ? requester.getFullName() : "N/A");

            partInfos.add(partInfo);

            totalParts++;
            if ("DELIVERED".equals(part.getRequestStatus())) {
                deliveredParts++;
            }
        }

        view.setParts(partInfos);

        // 6. Summary information
        SummaryInfo summary = new SummaryInfo();
        summary.setTotalDetails(totalDetails);
        summary.setCompletedDetails(completedDetails);
        summary.setTotalTasks(totalTasks);
        summary.setCompletedTasks(completedTasks);
        summary.setTotalParts(totalParts);
        summary.setDeliveredParts(deliveredParts);
        summary.setTotalEstimatedAmount(totalEstimatedAmount);
        summary.setTotalActualAmount(totalActualAmount);
        summary.setPendingApprovals(pendingApprovals);

        view.setSummary(summary);

        return view;
    }

    // Helper methods
    private String[] getDetailStatusInfo(String status) {
        switch (status) {
            case "PENDING":
                return new String[] { "Pending", "warning" };
            case "IN_PROGRESS":
                return new String[] { "In Progress", "primary" };
            case "COMPLETE":
                return new String[] { "Completed", "success" };
            default:
                return new String[] { "Unknown", "secondary" };
        }
    }

    private String[] getApprovalStatusInfo(String status) {
        switch (status) {
            case "PENDING":
                return new String[] { "Pending Approval", "warning" };
            case "APPROVED":
                return new String[] { "Approved", "success" };
            case "DECLINED":
                return new String[] { "Declined", "danger" };
            default:
                return new String[] { "N/A", "secondary" };
        }
    }

    private String getTaskTypeLabel(String type) {
        switch (type) {
            case "DIAGNOSIS":
                return "Diagnosis";
            case "REPAIR":
                return "Repair";
            case "OTHER":
                return "Other";
            default:
                return type;
        }
    }

    private String[] getPriorityInfo(String priority) {
        switch (priority) {
            case "URGENT":
                return new String[] { "Urgent", "danger" };
            case "HIGH":
                return new String[] { "High", "warning" };
            case "MEDIUM":
                return new String[] { "Medium", "info" };
            case "LOW":
                return new String[] { "Low", "secondary" };
            default:
                return new String[] { "Unknown", "secondary" };
        }
    }

    private String[] getTaskStatusInfo(String status) {
        switch (status) {
            case "ASSIGNED":
                return new String[] { "Assigned", "info" };
            case "IN_PROGRESS":
                return new String[] { "In Progress", "primary" };
            case "COMPLETE":
                return new String[] { "Completed", "success" };
            default:
                return new String[] { "Unknown", "secondary" };
        }
    }

    private String[] getPartStatusInfo(String status) {
        if (status == null) {
            return new String[] { "Unknown", "secondary" };
        }
        switch (status) {
            case "PENDING":
                return new String[] { "Pending", "warning" };
            case "AVAILABLE":
                return new String[] { "Available", "info" };
            case "DELIVERED":
                return new String[] { "Delivered", "success" };
            default:
                return new String[] { "Unknown", "secondary" };
        }
    }
}