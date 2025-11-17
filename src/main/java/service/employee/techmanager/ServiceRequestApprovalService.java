package service.employee.techmanager;

import dao.employee.admin.AdminDAO;
import model.employee.techmanager.PendingServiceRequestDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


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
