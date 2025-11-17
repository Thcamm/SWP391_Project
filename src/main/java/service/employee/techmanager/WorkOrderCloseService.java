package service.employee.techmanager;

import dao.employee.EmployeeDAO;
import dao.employee.techmanager.WorkOrderCloseDAO;
import model.employee.techmanager.WorkOrderCloseDTO;
// import service.notification.NotificationService; // (Tùy chọn)

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Work Order Closure business logic (GĐ7).
 * [REFACTORED] Handles business logic for closing work orders,
 * including new logic for CANCELLED tasks (checks for 0 Active Tasks).
 *
 * @author SWP391 Team
 * @version 3.1 (Unified Logic)
 */
public class WorkOrderCloseService {

    private final WorkOrderCloseDAO workOrderCloseDAO;
    private final EmployeeDAO employeeDAO;
    // private final NotificationService notificationService; // (Tùy chọn)

    // Constructor
    public WorkOrderCloseService() {
        this.workOrderCloseDAO = new WorkOrderCloseDAO();
        this.employeeDAO = new EmployeeDAO(); // Dùng để lấy EmployeeID
        // this.notificationService = new NotificationService(); // (Tùy chọn)
    }

    // Constructor for testing
    public WorkOrderCloseService(WorkOrderCloseDAO workOrderCloseDAO, EmployeeDAO employeeDAO) {
        this.workOrderCloseDAO = workOrderCloseDAO;
        this.employeeDAO = employeeDAO;
    }

    /**
     * [FIXED] Lấy TẤT CẢ WorkOrders đang IN_PROCESS của 1 TM.
     * (Gọi hàm DAO đã được thống nhất)
     */
    public List<WorkOrderCloseDTO> getAllInProgressWorkOrders(int techManagerId) throws SQLException {
        return workOrderCloseDAO.getAllInProgressWorkOrders(techManagerId);
    }

    /**
     * [FIXED] Đóng một WorkOrder sau khi xác minh.
     * Logic mới: Kiểm tra không còn task nào 'Active'.
     */
    public String closeWorkOrder(int workOrderID, int techManagerId) throws SQLException, IllegalStateException {

        // 1. Lấy thông tin VÀ xác thực quyền sở hữu
        WorkOrderCloseDTO workOrder = workOrderCloseDAO.getWorkOrderForVerification(workOrderID, techManagerId);

        if (workOrder == null) {
            throw new IllegalStateException(
                    "Work Order #" + workOrderID + " not found or you do not have permission to close it.");
        }

        // 2. [LOGIC ĐÚNG] Kiểm tra bằng hàm isReadyToClose()
        if (!workOrder.isReadyToClose()) {
            throw new IllegalStateException(
                    "Work Order #" + workOrderID + " cannot be closed. " +
                            workOrder.getActiveTasks() + " task(s) are still IN_PROGRESS or ASSIGNED.");
        }

        // 3. Nếu không còn task chạy -> Đóng Lệnh
        boolean success = workOrderCloseDAO.closeWorkOrder(workOrderID, techManagerId);

        if (!success) {
            throw new IllegalStateException(
                    "Failed to close Work Order #" + workOrderID +
                            ". Status may have changed. Please refresh and try again.");
        }
        return "Work Order #" + workOrderID + " closed successfully";
    }

    // =======================================================
    // CÁC HÀM CÒN LẠI (Cho Servlet và JSP)
    // =======================================================

    /**
     * Đếm số WorkOrder Sẵn sàng để đóng (cho Dashboard)
     */
    public int countWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersReadyForClosure(techManagerId);
    }

    /**
     * Đếm số WorkOrder đã đóng HÔM NAY (cho TM này)
     */
    public int countWorkOrdersClosedToday(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersByStatusAndDate(techManagerId, "COMPLETE", "DAY");
    }

    /**
     * Đếm số WorkOrder đã đóng THÁNG NÀY (cho TM này)
     */
    public int countWorkOrdersClosedThisMonth(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersByStatusAndDate(techManagerId, "COMPLETE", "MONTH");
    }

    /**
     * Lấy EmployeeID của TM từ userName (dùng cho Servlet).
     */
    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        return employeeDAO.getEmployeeIdByUserName(userName);
    }

    /**
     * Get a specific work order for verification before closing.
     */
    public WorkOrderCloseDTO getWorkOrderForVerification(int workOrderID, int techManagerId) throws SQLException {
        return workOrderCloseDAO.getWorkOrderForVerification(workOrderID, techManagerId);
    }

}