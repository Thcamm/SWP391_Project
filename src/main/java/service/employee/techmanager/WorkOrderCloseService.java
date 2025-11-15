package service.employee.techmanager;

import dao.employee.EmployeeDAO;
import dao.employee.techmanager.WorkOrderCloseDAO;
import model.employee.techmanager.WorkOrderCloseDTO;
// import service.notification.NotificationService; // (Tùy chọn)

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for WorkOrder closure operations (GĐ7).
 * [REFACTORED] Handles business logic for closing work orders,
 * including new logic for CANCELLED tasks (checks for 0 Active Tasks).
 *
 * @author SWP391 Team
 * @version 2.1 (Logic Fixed)
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

    // Constructor for testing (Giữ nguyên)
    public WorkOrderCloseService(WorkOrderCloseDAO workOrderCloseDAO, EmployeeDAO employeeDAO) {
        this.workOrderCloseDAO = workOrderCloseDAO;
        this.employeeDAO = employeeDAO;
    }

    /**
     * [FIXED] Lấy các WorkOrder sẵn sàng để đóng, LỌC THEO TM.
     * (Gọi hàm DAO đã được sửa)
     *
     * @param techManagerId ID của Tech Manager đang đăng nhập
     * @return List of WorkOrders ready to close
     * @throws SQLException if database error occurs
     */
    public List<WorkOrderCloseDTO> getWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        // Truyền techManagerId xuống DAO
        return workOrderCloseDAO.getWorkOrdersReadyForClosure(techManagerId);
    }

    /**
     * [FIXED] Đóng một WorkOrder sau khi xác minh.
     * Logic mới: Kiểm tra không còn task nào 'Active'.
     *
     * @param workOrderID   ID của WorkOrder
     * @param techManagerId ID của TM (để xác thực và ghi log)
     * @return true nếu đóng thành công
     * @throws SQLException
     * @throws IllegalStateException nếu nghiệp vụ thất bại
     */
    public boolean closeWorkOrder(int workOrderID, int techManagerId) throws SQLException, IllegalStateException {

        // 1. Lấy thông tin (Yêu cầu DAO phải cung cấp 'ActiveTasks')
        // (Lưu ý: getWorkOrderForClosure cũng nên lọc theo techManagerId)
        WorkOrderCloseDTO workOrder = workOrderCloseDAO.getWorkOrderForClosure(workOrderID);

        if (workOrder == null) {
            throw new IllegalStateException("Work Order #" + workOrderID + " not found.");
        }

        // 2. [LOGIC ĐÚNG] Kiểm tra bằng hàm isReadyToClose()
        // (Hàm này trong DTO sẽ kiểm tra: totalTasks > 0 && activeTasks == 0)
        if (!workOrder.isReadyToClose()) {
            throw new IllegalStateException(
                    "Work Order #" + workOrderID + " cannot be closed. " +
                            workOrder.getActiveTasks() + " task(s) are still IN_PROGRESS or ASSIGNED.");
        }

        // 3. Nếu không còn task chạy -> Đóng Lệnh
        // (Truyền cả techManagerId xuống DAO để xác thực)
        boolean success = workOrderCloseDAO.closeWorkOrder(workOrderID, techManagerId);

        if (!success) {
            // Lỗi này xảy ra nếu có race condition (ai đó gán lại task ngay khi TM nhấn
            // close)
            // hoặc WorkOrder không thuộc quyền quản lý của TM này
            throw new IllegalStateException(
                    "Failed to close Work Order #" + workOrderID +
                            ". Status may have changed or you are not the assigned manager.");
        }

        // 4. (TODO) Gửi Notification cho Kế toán
        // try {
        // notificationService.notifyAccountant(workOrderID, "WorkOrder #" + workOrderID
        // + " is complete and ready for invoicing.");
        // } catch (Exception e) {
        // System.err.println("Failed to send notification for WO #" + workOrderID);
        // }

        return true;
    }

    /**
     * [MỚI] Đếm số WorkOrder đã đóng HÔM NAY (cho TM này)
     */
    public int countWorkOrdersClosedToday(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersByStatusAndDate(techManagerId, "COMPLETE", "DAY");
    }

    /**
     * [MỚI] Đếm số WorkOrder đã đóng THÁNG NÀY (cho TM này)
     */
    public int countWorkOrdersClosedThisMonth(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersByStatusAndDate(techManagerId, "COMPLETE", "MONTH");
    }

    /**
     * [MỚI] Lấy EmployeeID của TM từ userName (dùng cho Servlet).
     */
    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        // Giả sử EmployeeDAO có hàm này
        return employeeDAO.getEmployeeIdByUserName(userName);
    }

    /**
     * Get a specific work order for verification before closing.
     *
     * @param workOrderID The ID of the WorkOrder
     * @return WorkOrderCloseDTO if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public WorkOrderCloseDTO getWorkOrderForVerification(int workOrderID) throws SQLException {
        // (Hàm này bị trùng lặp logic với closeWorkOrder, nhưng vẫn giữ nếu JSP khác
        // cần)
        return workOrderCloseDAO.getWorkOrderForClosure(workOrderID);
    }
}