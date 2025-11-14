package dao.inventory;

import common.DbContext;
import model.inventory.WorkOrderPart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderPartDAO extends DbContext {

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
