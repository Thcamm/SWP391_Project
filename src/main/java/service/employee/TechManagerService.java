package service.employee;

import dao.user.UserDAO;
import model.employee.techmanager.TechManager;
import model.user.User;
import model.workorder.WorkOrder;
import service.work.WorkOrderService;

import java.sql.SQLException;
import java.util.List;

public class TechManagerService {
    private UserDAO userDAO;
    private WorkOrderService workOrderService;

    public TechManagerService() {
        this.userDAO = new UserDAO();
        this.workOrderService = new WorkOrderService();
    }

    /**
     * Get TechManager by User ID
     * 
     * @param userId User ID
     * @return TechManager object or null if not found or not authorized
     */
    public TechManager getTechManagerByUserId(int userId) throws SQLException {
        System.out.println("TechManagerService DEBUG: getTechManagerByUserId called with userId=" + userId);
        User user = userDAO.getUserById(userId);
        System.out.println("TechManagerService DEBUG: userDAO.getUserById returned: "
                + (user != null ? "user found with roleId=" + user.getRoleId() : "null"));

        if (user != null && (user.getRoleId() == 1 || user.getRoleId() == 2)) { // Admin (1) or TechManager (2) role
            System.out.println("TechManagerService DEBUG: user has valid role (1 or 2), creating TechManager object");
            TechManager techManager = new TechManager();
            techManager.setUserId(userId);
            techManager.setUserName(user.getUserName());
            techManager.setFullName(user.getFullName());
            techManager.setEmail(user.getEmail());
            techManager.setRoleId(user.getRoleId());
            techManager.setActiveStatus(user.isActiveStatus());

            return techManager;
        }
        System.out.println("TechManagerService DEBUG: user not found or invalid role, returning null");
        return null;
    }

    /**
     * Get work orders for a user based on their role
     * Admin sees all work orders, TechManager sees only their own
     * 
     * @param user The user (Admin or TechManager)
     * @return List of work orders
     */
    public List<WorkOrder> getWorkOrdersForUser(TechManager user) throws SQLException {
        if (user.getRoleId() == 1) { // Admin - see all work orders
            return workOrderService.getAllWorkOrders();
        } else { // TechManager - see only their work orders
            return workOrderService.getWorkOrdersByTechManager(user.getEmployeeId());
        }
    }

    /**
     * Count work orders by status
     * 
     * @param workOrders List of work orders
     * @param status     Status to count
     * @return Count of work orders with the specified status
     */
    public int countWorkOrdersByStatus(List<WorkOrder> workOrders, WorkOrder.Status status) {
        return (int) workOrders.stream()
                .filter(wo -> status.equals(wo.getStatus()))
                .count();
    }

    /**
     * Get dashboard data for a user
     * 
     * @param user The user (Admin or TechManager)
     * @return Dashboard data containing work orders and statistics
     */
    public DashboardData getDashboardData(TechManager user) throws SQLException {
        List<WorkOrder> workOrders = getWorkOrdersForUser(user);

        DashboardData data = new DashboardData();
        data.setWorkOrders(workOrders);
        data.setPendingCount(countWorkOrdersByStatus(workOrders, WorkOrder.Status.PENDING));
        data.setInProcessCount(countWorkOrdersByStatus(workOrders, WorkOrder.Status.IN_PROCESS));
        data.setCompletedCount(countWorkOrdersByStatus(workOrders, WorkOrder.Status.COMPLETE));
        data.setIsAdmin(user.getRoleId() == 1);

        return data;
    }

    /**
     * Validate if a TechManager can access a specific work order
     * 
     * @param techManager The TechManager
     * @param workOrderId The work order ID
     * @return true if can access, false otherwise
     */
    public boolean canAccessWorkOrder(TechManager techManager, int workOrderId) throws SQLException {
        if (techManager.getRoleId() == 1) { // Admin can access all
            return true;
        }

        // TechManager can only access their own work orders
        WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
        return workOrder != null && workOrder.getTechManagerId() == techManager.getEmployeeId();
    }

    /**
     * Dashboard data container
     */
    public static class DashboardData {
        private List<WorkOrder> workOrders;
        private int pendingCount;
        private int inProcessCount;
        private int completedCount;
        private boolean isAdmin;

        // Getters and setters
        public List<WorkOrder> getWorkOrders() {
            return workOrders;
        }

        public void setWorkOrders(List<WorkOrder> workOrders) {
            this.workOrders = workOrders;
        }

        public int getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(int pendingCount) {
            this.pendingCount = pendingCount;
        }

        public int getInProcessCount() {
            return inProcessCount;
        }

        public void setInProcessCount(int inProcessCount) {
            this.inProcessCount = inProcessCount;
        }

        public int getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(int completedCount) {
            this.completedCount = completedCount;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
        }
    }
}
