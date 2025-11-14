package dao.inventory;

import common.DbContext;
import common.message.ServiceResult;
import model.employee.technician.PartOption;
import model.inventory.WorkOrderPart;
import model.inventory.WorkOrderPartView;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderPartDAO extends DbContext {

    public List<WorkOrderPartView> getPartsByAssignment(int assignmentId) throws SQLException {
        String sql = """
            SELECT 
                wop.WorkOrderPartID,
                wop.DetailID,
                wop.PartDetailID,
                wop.DiagnosticPartID,
                wop.QuantityUsed,
                wop.UnitPrice,
                wop.request_status,
                wop.requested_at,
                pd.SKU,
                pd.Quantity AS StockQty,
                p.PartCode,
                p.PartName,
                e.EmployeeID,
                u.FullName AS RequestedByName
            FROM TaskAssignment ta
            JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
            JOIN WorkOrderPart wop   ON wop.DetailID = wod.DetailID
            JOIN PartDetail pd       ON pd.PartDetailID = wop.PartDetailID
            JOIN Part p              ON p.PartID = pd.PartID
            LEFT JOIN Employee e     ON e.EmployeeID = wop.RequestedByID
            LEFT JOIN `User` u       ON u.UserID = e.UserID
            WHERE ta.AssignmentID = ?
            ORDER BY wop.requested_at DESC, wop.WorkOrderPartID DESC
            """;

        List<WorkOrderPartView> list = new ArrayList<>();
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkOrderPartView v = new WorkOrderPartView();
                    v.setWorkOrderPartID(rs.getInt("WorkOrderPartID"));
                    v.setDetailID(rs.getInt("DetailID"));
                    v.setPartDetailID(rs.getInt("PartDetailID"));
                    v.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
                    v.setQuantityUsed(rs.getInt("QuantityUsed"));
                    v.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    v.setRequestStatus(rs.getString("request_status"));
                    Timestamp ts = rs.getTimestamp("requested_at");
                    if (ts != null) v.setRequestedAt(ts.toLocalDateTime());

                    v.setSku(rs.getString("SKU"));
                    v.setCurrentStock(rs.getInt("StockQty"));
                    v.setPartCode(rs.getString("PartCode"));
                    v.setPartName(rs.getString("PartName"));
                    v.setRequestedByName(rs.getString("RequestedByName"));

                    list.add(v);
                }
            }
        }
        return list;
    }

    public ServiceResult createPartRequestForAssignment(
            int assignmentId,
            int partDetailId,
            int quantity
    ) throws SQLException {

        String getTaskSql = "SELECT DetailID, AssignToTechID FROM TaskAssignment WHERE AssignmentID = ?";
        String getPartSql = "SELECT UnitPrice, Quantity AS StockQty FROM PartDetail WHERE PartDetailID = ?";
        String insertSql  = """
        INSERT INTO WorkOrderPart
            (DetailID, PartDetailID, DiagnosticPartID,
             RequestedByID, QuantityUsed, UnitPrice,
             request_status, requested_at)
        VALUES (?, ?, NULL, ?, ?, ?, 'PENDING', NOW())
        """;

        try (Connection c = DbContext.getConnection()) {
            c.setAutoCommit(false);

            int detailId;
            int techId;
            try (PreparedStatement ps = c.prepareStatement(getTaskSql)) {
                ps.setInt(1, assignmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return ServiceResult.error("ERR134", "WOP", "Ko tim thay task");
                    }
                    detailId = rs.getInt("DetailID");
                    techId   = rs.getInt("AssignToTechID");
                }
            }

            BigDecimal unitPrice;
            int stockQty;
            try (PreparedStatement ps = c.prepareStatement(getPartSql)) {
                ps.setInt(1, partDetailId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return ServiceResult.error("ERR121", "PartDetail", "Khong tim thay part detail");
                    }
                    unitPrice = rs.getBigDecimal("UnitPrice");
                    stockQty  = rs.getInt("StockQty");
                }
            }

            if (quantity <= 0) {
                return ServiceResult.error("ERR555", "Quantity", "Số lượng phải > 0.");
            }
            if (quantity > stockQty) {
                return ServiceResult.error("ERR999","quantity", "Số lượng yêu cầu vượt tồn kho hiện tại.");
            }

            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, detailId);
                ps.setInt(2, partDetailId);
                ps.setInt(3, techId);
                ps.setInt(4, quantity);
                ps.setBigDecimal(5, unitPrice);
                ps.executeUpdate();
            }

            c.commit();
            return ServiceResult.success("Đã tạo yêu cầu phụ tùng, chờ kho xử lý.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<PartOption> getAvailablePartsForAssignment(int assignmentId) throws SQLException {
        String sql = """
        SELECT 
            pd.PartDetailID,
            pd.SKU,
            p.PartName,
             pd.Quantity AS CurrentStock
        FROM PartDetail pd
        JOIN Part p ON pd.PartID = p.PartID
        ORDER BY p.PartName ASC
    """;

        List<PartOption> list = new ArrayList<>();

        try (Connection con = DbContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PartOption opt = new PartOption();
                opt.setPartDetailId(rs.getInt("PartDetailID"));
                opt.setSku(rs.getString("SKU"));
                opt.setPartName(rs.getString("PartName"));
                opt.setCurrentStock(rs.getInt("CurrentStock"));
                list.add(opt);
            }
        }

        return list;
    }

    public boolean hasPendingRequestsForAssignment(int assignmentId) throws SQLException {
        final String sql = """
            SELECT 1
            FROM WorkOrderPart wop
            JOIN TaskAssignment ta ON ta.DetailID = wop.DetailID
            WHERE ta.AssignmentID = ?
              AND wop.request_status = 'PENDING'
            LIMIT 1
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // có bản ghi => còn pending
            }
        }
    }

    /**
     * Get all pending part requests
     */
    public List<WorkOrderPart> getPendingRequests() throws SQLException {
        List<WorkOrderPart> list = new ArrayList<>();
        String sql = "SELECT wop.*, pd.Quantity as CurrentStock, p.PartName " + // ✅ Đổi Name → PartName
                "FROM workorderpart wop " +
                "JOIN PartDetail pd ON wop.PartDetailID = pd.PartDetailID " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "WHERE wop.request_status = 'PENDING' " +
                "ORDER BY wop.requested_at ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                WorkOrderPart request = new WorkOrderPart();
                request.setWorkOrderPartId(rs.getInt("WorkOrderPartID"));
                request.setPartDetailID(rs.getInt("PartDetailID"));
                request.setQuantityUsed(rs.getInt("QuantityUsed"));
                request.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                request.setRequestStatus(rs.getString("request_status"));
                request.setRequestedAt(rs.getTimestamp("requested_at").toLocalDateTime());

                request.setPartName(rs.getString("PartName"));

                list.add(request);
            }
        }
        return list;
    }


    /**
     * Approve request and deduct stock
     * @return true if approved successfully, false if insufficient stock
     */
    public boolean approveRequest(int workOrderPartId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Get request details
            String selectSql = "SELECT wop.PartDetailID, wop.QuantityUsed, pd.Quantity " +
                    "FROM workorderpart wop " +
                    "JOIN PartDetail pd ON wop.PartDetailID = pd.PartDetailID " +
                    "WHERE wop.WorkOrderPartID = ? AND wop.request_status = 'PENDING'";

            int partDetailId;
            int quantityRequested;
            int currentStock;

            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, workOrderPartId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return false; // Request not found or not pending
                }

                partDetailId = rs.getInt("PartDetailID");
                quantityRequested = rs.getInt("QuantityUsed");
                currentStock = rs.getInt("Quantity");
            }

            // 2. Validate stock
            if (currentStock < quantityRequested) {
                // Insufficient stock - reject
                String rejectSql = "UPDATE workorderpart SET request_status = 'REJECTED' WHERE WorkOrderPartID = ?";
                try (PreparedStatement ps = conn.prepareStatement(rejectSql)) {
                    ps.setInt(1, workOrderPartId);
                    ps.executeUpdate();
                }
                conn.commit();
                return false;
            }

            // 3. Deduct stock
            String updateStockSql = "UPDATE PartDetail SET Quantity = Quantity - ? WHERE PartDetailID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                ps.setInt(1, quantityRequested);
                ps.setInt(2, partDetailId);
                ps.executeUpdate();
            }

            // 4. Approve request
            String approveSql = "UPDATE workorderpart SET request_status = 'APPROVED' WHERE WorkOrderPartID = ?";
            try (PreparedStatement ps = conn.prepareStatement(approveSql)) {
                ps.setInt(1, workOrderPartId);
                ps.executeUpdate();
            }

            // 5. Create inventory transaction (OUT)
            String insertTransactionSql = "INSERT INTO InventoryTransaction " +
                    "(PartID, PartDetailID, TransactionType, TransactionDate, StoreKeeperID, Note, Quantity) " +
                    "SELECT pd.PartID, pd.PartDetailID, 'OUT', NOW(), ?, " +
                    "CONCAT('Work Order Part Request #', ?), ? " +
                    "FROM PartDetail pd WHERE pd.PartDetailID = ?";

            try (PreparedStatement ps = conn.prepareStatement(insertTransactionSql)) {
                ps.setInt(1, 1); // StoreKeeperID - get from session
                ps.setInt(2, workOrderPartId);
                ps.setInt(3, quantityRequested);
                ps.setInt(4, partDetailId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Manually reject request
     */
    public boolean rejectRequest(int workOrderPartId, String reason) throws SQLException {
        String sql = "UPDATE workorderpart SET request_status = 'REJECTED' WHERE WorkOrderPartID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderPartId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
