package service.carservice;

import dao.carservice.ServiceRequestDAO;
import dao.misc.NotificationDAO;
import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.misc.Notification;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServiceRequestService {
    private final ServiceRequestDAO dao = new ServiceRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final WorkOrderDetailDAO workOrderDetailDAO = new WorkOrderDetailDAO();

    public boolean createRequestAndNotify(ServiceRequest request, List<Integer> serviceIds, List<Integer> recipientIds)
            throws SQLException {
        int requestId = dao.createServiceRequestWithDetails(request, serviceIds);
        if (requestId <= 0)
            return false;

        if (recipientIds != null) {
            for (Integer userId : recipientIds) {
                Notification notif = new Notification();
                notif.setUserId(userId);
                notif.setTitle("New Service Request Created");
                notif.setBody("A new service request #" + requestId + " needs review.");
                notif.setEntityType("SERVICE_REQUEST");
                notif.setEntityId(requestId);
                if (request.getAppointmentID() != null)
                    notif.setAppointmentId(request.getAppointmentID());
                notificationDAO.createNotification(notif);
            }
        }
        return true;
    }

    /**
     * Approve ServiceRequest and Create WorkOrder with initial DIAGNOSIS detail
     * 
     * @param requestId             ServiceRequest ID to approve
     * @param techManagerEmployeeId TechManager's Employee ID
     * @param taskDescription       Description for diagnosis task
     * @return WorkOrderID if successful, -1 if failed, -2 if request not in PENDING
     *         status
     * @throws SQLException if database error occurs
     */
    public int approveServiceRequestAndCreateWorkOrder(int requestId, int techManagerEmployeeId, String taskDescription)
            throws SQLException {
        Connection conn = null;
        try {
            conn = common.DbContext.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check if ServiceRequest is PENDING
            ServiceRequest serviceRequest = dao.getServiceRequestForUpdate(conn, requestId);
            if (serviceRequest == null || !serviceRequest.getStatus().equals("PENDING")) {
                conn.rollback();
                return -2; // Not in PENDING status
            }

            // Step 2: Update ServiceRequest status to APPROVE
            boolean statusUpdated = dao.updateServiceRequestStatus(conn, requestId, "APPROVE");
            if (!statusUpdated) {
                conn.rollback();
                return -1;
            }

            // Step 3: Create WorkOrder
            WorkOrder workOrder = new WorkOrder();
            workOrder.setTechManagerId(techManagerEmployeeId);
            workOrder.setRequestId(requestId);
            workOrder.setEstimateAmount(BigDecimal.ZERO); // Initial estimate
            workOrder.setStatus(WorkOrder.Status.IN_PROCESS);

            int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
            if (workOrderId <= 0) {
                conn.rollback();
                return -1;
            }

            // Step 4: Create initial DIAGNOSIS WorkOrderDetail
            WorkOrderDetail diagnosisDetail = new WorkOrderDetail();
            diagnosisDetail.setWorkOrderId(workOrderId);
            diagnosisDetail.setSource(WorkOrderDetail.Source.REQUEST); // From customer request
            diagnosisDetail.setTaskDescription(
                    taskDescription != null ? taskDescription : "Chẩn đoán tổng quát tình trạng xe");
            diagnosisDetail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.APPROVED); // Auto-approved (customer
                                                                                        // agreed to diagnosis)
            diagnosisDetail.setEstimateHours(BigDecimal.valueOf(1.0)); // Default 1 hour for diagnosis
            diagnosisDetail.setEstimateAmount(BigDecimal.ZERO);

            int detailId = workOrderDetailDAO.createWorkOrderDetail(diagnosisDetail);
            if (detailId <= 0) {
                conn.rollback();
                return -1;
            }

            conn.commit();
            return workOrderId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}