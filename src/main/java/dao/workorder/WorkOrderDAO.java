package dao.workorder;

import common.DbContext;
import model.customer.Customer;
import model.dto.InvoiceItemDTO;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDAO extends DbContext {

    public int createWorkOrder(WorkOrder workOrder) throws SQLException {
        String sql = "INSERT INTO WorkOrder (TechManagerID, RequestID, EstimateAmount, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, workOrder.getTechManagerId());
            ps.setInt(2, workOrder.getRequestId());
            ps.setBigDecimal(3, workOrder.getEstimateAmount());
            ps.setString(4, workOrder.getStatus().name());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Create WorkOrder using provided connection (for transactions)
     */
    public int createWorkOrder(Connection conn, WorkOrder workOrder) throws SQLException {
        String sql = "INSERT INTO WorkOrder (TechManagerID, RequestID, EstimateAmount, Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, workOrder.getTechManagerId());
            ps.setInt(2, workOrder.getRequestId());
            ps.setBigDecimal(3, workOrder.getEstimateAmount());
            ps.setString(4, workOrder.getStatus().name());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    public WorkOrder getWorkOrderById(int workOrderId) throws SQLException {
        String sql = "SELECT * FROM WorkOrder WHERE WorkOrderID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractWorkOrder(rs);
                }
            }
        }
        return null;
    }

    public List<WorkOrder> getWorkOrdersByTechManager(int techManagerId) throws SQLException {
        String sql = "SELECT * FROM WorkOrder WHERE TechManagerID = ?";
        List<WorkOrder> workOrders = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workOrders.add(extractWorkOrder(rs));
                }
            }
        }
        return workOrders;
    }

    public List<WorkOrder> getAllWorkOrders() throws SQLException {
        String sql = "SELECT * FROM WorkOrder ORDER BY CreatedAt DESC";
        List<WorkOrder> workOrders = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                workOrders.add(extractWorkOrder(rs));
            }
        }
        return workOrders;
    }

    public boolean updateWorkOrderStatus(int workOrderId, WorkOrder.Status status) throws SQLException {
        String sql = "UPDATE WorkOrder SET Status = ? WHERE WorkOrderID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, workOrderId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean addWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, Source, DiagnosticID, ApprovalStatus, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getWorkOrderId());
            ps.setString(2, detail.getSource().name());
            if (detail.getDiagnosticId() != null) {
                ps.setInt(3, detail.getDiagnosticId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, detail.getApprovalStatus().name());
            ps.setString(5, detail.getTaskDescription());
            ps.setBigDecimal(6, detail.getEstimateHours());
            ps.setBigDecimal(7, detail.getEstimateAmount());
            return ps.executeUpdate() > 0;
        }
    }
    /**
     * Get customer ID from request ID
     */
    public int getCustomerIdByRequestId(int requestId) throws SQLException {
        String query = "SELECT CustomerID FROM Request WHERE RequestID = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CustomerID");
                }
            }
        }
        return -1; // Not found
    }
    public Customer getCustomerForWorkOrder(int workOrderId) throws SQLException {
        // This assumes CustomerDAO has getCustomerById method joining User table
        String sql = "SELECT c.*, u.FullName, u.Email, u.PhoneNumber " + // Select details from User
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " + // Join User table
                "WHERE wo.WorkOrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    // Map data from both Customer and User tables
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    return customer;
                }
            }
        }
        return null;
    }


    /**
     * Get WorkOrderDetails for Invoice display
     * COMPLETELY REWRITTEN - Safe version with fallback
     */
    public List<WorkOrderDetail> getWorkOrderDetailsForInvoice(int workOrderID) throws SQLException {
        List<WorkOrderDetail> details = new ArrayList<>();

        // Use backticks to ensure column name recognition
        String sql = "SELECT " +
                "`DetailID`, " +
                "`WorkOrderID`, " +
                "`source`, " +
                "`diagnostic_id`, " +
                "`approval_status`, " +
                "`approved_by_user_id`, " +
                "`approved_at`, " +
                "`TaskDescription`, " +  // With backticks
                "`EstimateHours`, " +
                "`EstimateAmount`, " +
                "`ActualHours` " +
                "FROM `garage_mgmt`.`WorkOrderDetail` " +  // Explicit database name
                "WHERE `WorkOrderID` = ? " +
                "ORDER BY `DetailID`";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbContext.getConnection();

            // Verify we're connected to correct database
            String currentDB = conn.getCatalog();
            if (!currentDB.equals("garage_mgmt")) {
                System.err.println("WARNING: Connected to wrong database: " + currentDB);
                conn.setCatalog("garage_mgmt");
            }

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workOrderID);

            rs = stmt.executeQuery();

            while (rs.next()) {
                WorkOrderDetail detail = new WorkOrderDetail();

                // Use column index to avoid name issues
                detail.setDetailId(rs.getInt(1));           // DetailID
                detail.setWorkOrderId(rs.getInt(2));        // WorkOrderID

                String source = rs.getString(3);             // source
                detail.setSource(source != null ? source : "REQUEST");

                String taskDesc = rs.getString(8);           // TaskDescription (column 8)
                detail.setTaskDescription(taskDesc != null && !taskDesc.trim().isEmpty()
                        ? taskDesc
                        : "General service task");

                BigDecimal estimateHours = rs.getBigDecimal(9);  // EstimateHours
                detail.setEstimateHours(estimateHours != null ? estimateHours : BigDecimal.ZERO);

                BigDecimal estimateAmount = rs.getBigDecimal(10); // EstimateAmount
                detail.setEstimateAmount(estimateAmount != null ? estimateAmount : BigDecimal.ZERO);

                BigDecimal actualHours = rs.getBigDecimal(11);    // ActualHours
                detail.setActualHours(actualHours != null ? actualHours : BigDecimal.ZERO);

                details.add(detail);
            }

        } catch (SQLException e) {
            System.err.println("ERROR in getWorkOrderDetailsForInvoice:");
            System.err.println("  WorkOrderID: " + workOrderID);
            System.err.println("  SQL: " + sql);
            System.err.println("  Error: " + e.getMessage());
            throw e;

        } finally {
            // Clean up resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignore */ }
        }

        return details;
    }

    /**
     * Retrieves all parts used for a specific WorkOrder.
     * @param workOrderId The ID of the WorkOrder.
     * @return A list of InvoiceItemDTO representing parts.
     */
    public List<InvoiceItemDTO> getWorkOrderPartsForInvoice(int workOrderId) throws SQLException {
        List<InvoiceItemDTO> items = new ArrayList<>();
        // Joins WorkOrderPart with PartDetail and Part to get name and price
        String sql = "SELECT p.PartName, wop.QuantityUsed, wop.UnitPrice " +
                "FROM WorkOrderPart wop " +
                "JOIN WorkOrderDetail wod ON wop.DetailID = wod.DetailID " + // Join needed if WorkOrderPart relates to DetailID
                "JOIN PartDetail pd ON wop.PartDetailID = pd.PartDetailID " + // Optional: If UnitPrice is NOT in WorkOrderPart
                "JOIN Part p ON pd.PartID = p.PartID " + // To get PartName
                "WHERE wod.WorkOrderID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItemDTO item = new InvoiceItemDTO();
                    item.setDescription(rs.getString("PartName"));
                    int quantity = rs.getInt("QuantityUsed");
                    BigDecimal unitPrice = rs.getBigDecimal("UnitPrice"); // Get price recorded at time of use
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    // Calculate amount
                    item.setAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Add WorkOrderDetail using provided connection (for transactions)
     */
    public int addWorkOrderDetail(Connection conn, WorkOrderDetail detail) throws SQLException {
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, Source, DiagnosticID, ApprovalStatus, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detail.getWorkOrderId());
            ps.setString(2, detail.getSource().name());
            if (detail.getDiagnosticId() != null) {
                ps.setInt(3, detail.getDiagnosticId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (detail.getApprovalStatus() != null) {
                ps.setString(4, detail.getApprovalStatus().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, detail.getTaskDescription());
            ps.setBigDecimal(6, detail.getEstimateHours());
            ps.setBigDecimal(7, detail.getEstimateAmount());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    // Helper method to map ResultSet to WorkOrder
    private WorkOrder extractWorkOrder(ResultSet rs) throws SQLException {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(rs.getInt("WorkOrderID"));
        workOrder.setTechManagerId(rs.getInt("TechManagerID"));
        workOrder.setRequestId(rs.getInt("RequestID"));
        workOrder.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));
        workOrder.setStatus(WorkOrder.Status.valueOf(rs.getString("Status")));
        workOrder.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return workOrder;
    }

    private void maybeCloseWorkOrder(Connection c, int detailId) throws SQLException {
        Integer woId = null;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT WorkOrderID FROM WorkOrderDetail WHERE DetailID = ?")) {
            ps.setInt(1, detailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) woId = rs.getInt(1);
            }
        }
        if (woId == null) return;

        // còn WOD nào chưa complete?
        boolean anyNotComplete;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM WorkOrderDetail WHERE WorkOrderID = ? AND (detail_status IS NULL OR detail_status <> 'COMPLETE') LIMIT 1")) {
            ps.setInt(1, woId);
            try (ResultSet rs = ps.executeQuery()) {
                anyNotComplete = rs.next();
            }
        }
        if (!anyNotComplete) {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE WorkOrder SET Status = 'COMPLETE' WHERE WorkOrderID = ?")) {
                ps.setInt(1, woId);
                ps.executeUpdate();
            }
        }
    }

}
