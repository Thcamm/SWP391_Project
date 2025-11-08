package service.employee.techmanager;

import dao.carservice.ServiceRequestDAO;
import dao.employee.admin.AdminDAO;
import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.dto.ServiceRequestViewDTO;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for Service Request Approval business logic (GĐ0 → GĐ1).
 * Manages service request approval and initial WorkOrder creation.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class ServiceRequestApprovalService {

    private final ServiceRequestDAO serviceRequestDAO;
    private final WorkOrderDAO workOrderDAO;
    private final WorkOrderDetailDAO workOrderDetailDAO;
    private final AdminDAO adminDAO;

    public ServiceRequestApprovalService() {
        this.serviceRequestDAO = new ServiceRequestDAO();
        this.workOrderDAO = new WorkOrderDAO();
        this.workOrderDetailDAO = new WorkOrderDetailDAO();
        this.adminDAO = new AdminDAO();
    }

    /**
     * Get TechManager's employee ID by username.
     * 
     * @param userName username from session
     * @return employee ID or null if not found
     * @throws SQLException if database error occurs
     */
    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        return adminDAO.getEmployeeIdByUsername(userName);
    }

    /**
     * Get all pending service requests.
     * 
     * @return list of pending service requests
     * @throws SQLException if database error occurs
     */
    public List<ServiceRequestViewDTO> getPendingServiceRequests() throws SQLException {
        return serviceRequestDAO.getPendingServiceRequests();
    }

    /**
     * Approve service request and create WorkOrder with initial DIAGNOSIS detail.
     * This is a transactional operation (GĐ0 → GĐ1 transition).
     * 
     * @param conn                  database connection (for transaction management)
     * @param requestId             service request ID
     * @param taskDescription       diagnosis task description
     * @param techManagerEmployeeId TechManager's employee ID
     * @return created WorkOrder ID if successful
     * @throws SQLException          if database error occurs
     * @throws IllegalStateException if service request is not in PENDING status
     */
    public int approveServiceRequest(Connection conn, int requestId, String taskDescription,
            int techManagerEmployeeId) throws SQLException {

        // Step 1: Check ServiceRequest status
        model.workorder.ServiceRequest serviceRequest = serviceRequestDAO.getServiceRequestForUpdate(conn, requestId);
        if (serviceRequest == null) {
            throw new IllegalArgumentException("Service Request not found");
        }

        if (!"PENDING".equals(serviceRequest.getStatus())) {
            throw new IllegalStateException("Service Request is not in PENDING status");
        }

        // Step 2: Update ServiceRequest to APPROVE
        boolean statusUpdated = serviceRequestDAO.updateServiceRequestStatus(conn, requestId, "APPROVE");
        if (!statusUpdated) {
            throw new SQLException("Failed to update Service Request status");
        }

        // Step 3: Create WorkOrder
        WorkOrder workOrder = new WorkOrder();
        workOrder.setTechManagerId(techManagerEmployeeId);
        workOrder.setRequestId(requestId);
        workOrder.setEstimateAmount(BigDecimal.ZERO);
        workOrder.setStatus(WorkOrder.Status.IN_PROCESS);

        int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
        if (workOrderId <= 0) {
            throw new SQLException("Failed to create WorkOrder");
        }

        // Step 4: Create initial DIAGNOSIS WorkOrderDetail
        WorkOrderDetail diagnosisDetail = new WorkOrderDetail();
        diagnosisDetail.setWorkOrderId(workOrderId);
        diagnosisDetail.setSource(WorkOrderDetail.Source.REQUEST);
        diagnosisDetail.setTaskDescription(
                taskDescription != null && !taskDescription.trim().isEmpty()
                        ? taskDescription
                        : "Chẩn đoán tổng quát tình trạng xe");
        diagnosisDetail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.APPROVED);
        diagnosisDetail.setEstimateHours(BigDecimal.valueOf(1.0));
        diagnosisDetail.setEstimateAmount(BigDecimal.ZERO);

        int detailId = workOrderDetailDAO.createWorkOrderDetail(conn, diagnosisDetail);
        if (detailId <= 0) {
            throw new SQLException("Failed to create WorkOrderDetail");
        }

        return workOrderId;
    }

    /**
     * Reject service request.
     * 
     * @param requestId service request ID
     * @param reason    rejection reason (currently unused, for future enhancement)
     * @return true if successful
     * @throws SQLException if database error occurs
     */
    public boolean rejectServiceRequest(int requestId, String reason) throws SQLException {
        return serviceRequestDAO.updateServiceRequestStatus(requestId, "REJECTED");
    }
}
