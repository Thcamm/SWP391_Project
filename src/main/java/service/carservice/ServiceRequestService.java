package service.carservice;

import dao.carservice.ServiceRequestDAO;
import dao.misc.NotificationDAO;
import dao.workorder.WorkOrderDAO;
import model.misc.Notification;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import common.DbContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServiceRequestService {
    private final ServiceRequestDAO dao = new ServiceRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();

    public boolean createRequestAndNotify(ServiceRequest request, List<Integer> serviceIds, List<Integer> recipientIds) throws SQLException {
        int requestId = dao.createServiceRequestWithDetails(request, serviceIds);
        if(requestId <= 0) return false;

        if(recipientIds != null) {
            for(Integer userId : recipientIds) {
                Notification notif = new Notification();
                notif.setUserId(userId);
                notif.setTitle("New Service Request Created");
                notif.setBody("A new service request #" + requestId + " needs review.");
                notif.setEntityType("SERVICE_REQUEST");
                notif.setEntityId(requestId);
                if(request.getAppointmentID()!=null) notif.setAppointmentId(request.getAppointmentID());
                notificationDAO.createNotification(notif);
            }
        }
        return true;
    }

    /**
     * TECHMANAGER: Approve ServiceRequest and create WorkOrder in ONE transaction
     * 
     * @param requestId Service request to approve
     * @param techManagerEmployeeId TechManager who approves (EmployeeID)
     * @param initialTaskDescription Description for initial WorkOrderDetail (from REQUEST)
     * @return WorkOrderID if success, -1 if failure, -2 if already approved/not pending
     */
    public int approveServiceRequestAndCreateWorkOrder(
            int requestId, 
            int techManagerEmployeeId, 
            String initialTaskDescription) {
        
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);
            
            // Step 1: Lock and check ServiceRequest status
            ServiceRequest sr = serviceRequestDAO.getServiceRequestForUpdate(conn, requestId);
            
            if (sr == null) {
                conn.rollback();
                System.err.println("[ServiceRequestService] RequestID " + requestId + " not found");
                return -1;
            }
            
            if (!"PENDING".equalsIgnoreCase(sr.getStatus())) {
                conn.rollback();
                System.err.println("[ServiceRequestService] RequestID " + requestId + " is not PENDING (current: " + sr.getStatus() + ")");
                return -2; // Already processed
            }
            
            // Step 2: Update ServiceRequest status to APPROVE
            boolean updated = serviceRequestDAO.updateServiceRequestStatus(conn, requestId, "APPROVE");
            if (!updated) {
                conn.rollback();
                System.err.println("[ServiceRequestService] Failed to update request status");
                return -1;
            }
            
            // Step 3: Create WorkOrder
            WorkOrder workOrder = new WorkOrder();
            workOrder.setTechManagerId(techManagerEmployeeId);
            workOrder.setRequestId(requestId);
            workOrder.setEstimateAmount(null); // Will be calculated from details
            workOrder.setStatus(WorkOrder.Status.PENDING);
            
            int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
            if (workOrderId <= 0) {
                conn.rollback();
                System.err.println("[ServiceRequestService] Failed to create WorkOrder");
                return -1;
            }
            
            // Step 4: Create initial WorkOrderDetail (source = REQUEST)
            WorkOrderDetail initialDetail = new WorkOrderDetail();
            initialDetail.setWorkOrderId(workOrderId);
            initialDetail.setSource(WorkOrderDetail.Source.REQUEST);
            initialDetail.setDiagnosticId(null);
            initialDetail.setApprovalStatus(null); // Not needed for REQUEST source
            initialDetail.setTaskDescription(
                initialTaskDescription != null ? initialTaskDescription : "Initial service: " + sr.getServiceID()
            );
            initialDetail.setEstimateHours(BigDecimal.ZERO);
            initialDetail.setEstimateAmount(BigDecimal.ZERO);
            
            int detailId = workOrderDAO.addWorkOrderDetail(conn, initialDetail);
            if (detailId <= 0) {
                conn.rollback();
                System.err.println("[ServiceRequestService] Failed to create initial WorkOrderDetail");
                return -1;
            }
            
            // Step 5: Send notification (best-effort, won't rollback if fails)
            try {
                Notification notif = new Notification();
                notif.setUserId(techManagerEmployeeId); // Notify TechManager (or could notify customer)
                notif.setTitle("Work Order Created");
                notif.setBody("WorkOrder #" + workOrderId + " created for ServiceRequest #" + requestId);
                notif.setEntityType("WORKORDER");
                notif.setEntityId(workOrderId);
                notificationDAO.createNotification(notif);
            } catch (Exception e) {
                System.err.println("[ServiceRequestService] Notification failed (non-critical): " + e.getMessage());
            }
            
            // Commit transaction
            conn.commit();
            System.out.println("[ServiceRequestService] SUCCESS: Approved RequestID=" + requestId + 
                             ", Created WorkOrderID=" + workOrderId + ", DetailID=" + detailId);
            return workOrderId;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[ServiceRequestService] Rollback failed: " + ex.getMessage());
                }
            }
            System.err.println("[ServiceRequestService] Transaction failed: " + e.getMessage());
            e.printStackTrace();
            return -1;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("[ServiceRequestService] Failed to close connection: " + e.getMessage());
                }
            }
        }
    }
}