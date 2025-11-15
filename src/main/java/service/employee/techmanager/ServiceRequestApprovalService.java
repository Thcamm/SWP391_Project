package service.employee.techmanager;

import dao.employee.admin.AdminDAO;
import model.employee.techmanager.PendingServiceRequestDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * DEPRECATED - Partial Replacement by TechManagerService
 * 
 * Service for Service Request Approval business logic (GĐ0 → GĐ1).
 * 
 * ⚠ LUỒNG 4.0 (Merged Workflow):
 * - Approval logic has been MOVED to
 * TechManagerService.approveAndClassifyServiceRequest()
 * - This class now only provides UTILITY methods for
 * ServiceRequestApprovalServlet
 * 
 * KEPT METHODS (Utility):
 * - getPendingServiceRequests() - Query service requests
 * - getTechManagerEmployeeId() - Get employee ID
 * - rejectServiceRequest() - Handle rejections
 * 
 * @author SWP391 Team
 * @version 4.0 (Merged Workflow)
 * @deprecated Use TechManagerService for approval logic. This class only
 *             provides utilities.
 */
@Deprecated
public class ServiceRequestApprovalService {

    private final dao.employee.techmanager.ServiceRequestDAO serviceRequestDAO;
    private final dao.carservice.ServiceRequestDAO carServiceRequestDAO; // For getServiceRequestDetails
    private final AdminDAO adminDAO;

    public ServiceRequestApprovalService() {
        this.serviceRequestDAO = new dao.employee.techmanager.ServiceRequestDAO();
        this.carServiceRequestDAO = new dao.carservice.ServiceRequestDAO();
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
     * Get all pending service requests with full details.
     * 
     * @return list of pending service requests
     * @throws SQLException if database error occurs
     */
    public List<PendingServiceRequestDTO> getPendingServiceRequests() throws SQLException {
        return serviceRequestDAO.getPendingServiceRequests();
    }

    /**
     * DELETED - LUỒNG CŨ (OLD WORKFLOW)
     * 
     * This method has been REPLACED by:
     * TechManagerService.approveServiceRequestAndCreateWorkOrder()
     * 
     * OLD LOGIC (Deleted):
     * - Created 1 WorkOrder + 1 WorkOrderDetail "Chẩn đoán tổng quát"
     * - Set source=REQUEST, approval_status=APPROVED immediately
     * 
     * NEW LOGIC (LUỒNG MỚI - in TechManagerService):
     * - Creates 1 WorkOrder + N WorkOrderDetails (one per service)
     * - Each WOD has source=NULL, approval_status=PENDING
     * - Redirects to Triage screen (GĐ 2) for classification
     * 
     * @deprecated Use TechManagerService.approveServiceRequestAndCreateWorkOrder()
     *             instead
     */
    @Deprecated
    private void approveServiceRequest_OLD_LOGIC_DELETED() {
        throw new UnsupportedOperationException(
                "LUỒNG CŨ - This method has been deleted. " +
                        "Use TechManagerService.approveServiceRequestAndCreateWorkOrder() instead. " +
                        "See LUỒNG MỚI documentation in TechManagerService.");
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
        try (Connection conn = common.DbContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                serviceRequestDAO.updateServiceRequestStatus(conn, requestId, "REJECTED");
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * LUỒNG 4.0: Get services for a specific request
     */
    public List<model.workorder.ServiceRequestDetail> getServicesForRequest(int requestId) throws SQLException {
        return carServiceRequestDAO.getServiceRequestDetails(requestId);
    }
}
