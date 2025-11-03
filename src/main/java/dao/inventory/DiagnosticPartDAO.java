package dao.inventory;

import model.inventory.DiagnosticPart;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for DiagnosticPart operations
 * Handles parts associated with vehicle diagnostics
 *
 * @author vuthithuy-qh
 * @version 1.0
 * @since 2025-11-01
 */
public class DiagnosticPartDAO {

    // ================================================================
    // CREATE OPERATIONS
    // ================================================================

    /**
     * Thêm part vào diagnostic
     */
    public int addPartToDiagnostic(Connection conn, DiagnosticPart part) throws SQLException {
        String sql = "INSERT INTO DiagnosticPart " +
                "(VehicleDiagnosticID, PartDetailID, QuantityNeeded, UnitPrice, " +
                "PartCondition, ReasonForReplacement, IsApproved) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, part.getVehicleDiagnosticID());
            ps.setInt(2, part.getPartDetailID());
            ps.setInt(3, part.getQuantityNeeded());
            ps.setBigDecimal(4, part.getUnitPrice() == null ? BigDecimal.ZERO : part.getUnitPrice());
            ps.setString(5, part.getPartCondition().name());
            ps.setString(6, part.getReasonForReplacement() == null ? "" : part.getReasonForReplacement());
            ps.setBoolean(7, part.isApproved());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1); // an toàn với BIGINT
                        return (id > Integer.MAX_VALUE) ? -1 : (int) id;
                    }
                }
            }
        }
        return -1;
    }


    // ================================================================
    // READ OPERATIONS - SINGLE RECORD
    // ================================================================

    /**
     * Lấy DiagnosticPart theo ID
     */
    public DiagnosticPart getDiagnosticPartById(Connection conn, int diagnosticPartId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    dp.*, " +
                        "    p.PartCode, " +
                        "    p.PartName, " +
                        "    p.Category, " +
                        "    pd.SKU, " +
                        "    pd.Quantity AS CurrentStock " +
                        "FROM DiagnosticPart dp " +
                        "JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID " +
                        "JOIN Part p ON pd.PartID = p.PartID " +
                        "WHERE dp.DiagnosticPartID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticPartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDiagnosticPart(rs);
                }
            }
        }
        return null;
    }

    // ================================================================
    // READ OPERATIONS - LISTS
    // ================================================================

    /**
     * Lấy danh sách parts của một diagnostic (với thông tin đầy đủ)
     */
    public List<DiagnosticPart> getPartsByDiagnosticId(Connection conn, int diagnosticId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    dp.*, " +
                        "    p.PartCode, " +
                        "    p.PartName, " +
                        "    p.Category, " +
                        "    pd.SKU, " +
                        "    pd.Quantity AS CurrentStock " +
                        "FROM DiagnosticPart dp " +
                        "JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID " +
                        "JOIN Part p ON pd.PartID = p.PartID " +
                        "WHERE dp.VehicleDiagnosticID = ? " +
                        "ORDER BY " +
                        "    FIELD(dp.PartCondition, 'REQUIRED', 'RECOMMENDED', 'OPTIONAL'), " +
                        "    p.PartName";

        List<DiagnosticPart> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiagnosticPart(rs));
                }
            }
        }
        return list;
    }

    /**
     * Lấy tất cả parts chưa được approve (cho Manager review)
     */
    public List<DiagnosticPart> getUnapprovedParts(Connection conn) throws SQLException {
        String sql =
                "SELECT " +
                        "    dp.*, " +
                        "    p.PartCode, " +
                        "    p.PartName, " +
                        "    p.Category, " +
                        "    pd.SKU, " +
                        "    pd.Quantity AS CurrentStock, " +
                        "    vd.IssueFound, " +
                        "    vd.EstimateCost, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    u.FullName AS TechName " +
                        "FROM DiagnosticPart dp " +
                        "JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID " +
                        "JOIN Part p ON pd.PartID = p.PartID " +
                        "JOIN VehicleDiagnostic vd ON dp.VehicleDiagnosticID = vd.VehicleDiagnosticID " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u ON e.UserID = u.UserID " +
                        "WHERE dp.IsApproved = 0 AND vd.Status = 1 " +
                        "ORDER BY " +
                        "    FIELD(dp.PartCondition, 'REQUIRED', 'RECOMMENDED', 'OPTIONAL'), " +
                        "    vd.CreatedAt DESC";

        List<DiagnosticPart> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DiagnosticPart dp = mapDiagnosticPart(rs);

                // Thêm thông tin context
                String vehicleInfo = String.format("%s - %s %s",
                        rs.getString("LicensePlate"),
                        rs.getString("Brand"),
                        rs.getString("Model")
                );
                dp.setVehicleInfo(vehicleInfo);
                dp.setTechnicianName(rs.getString("TechName"));

                list.add(dp);
            }
        }
        return list;
    }

    /**
     * Lấy approved parts của một diagnostic
     */
    public List<DiagnosticPart> getApprovedPartsByDiagnosticId(Connection conn, int diagnosticId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    dp.*, " +
                        "    p.PartCode, " +
                        "    p.PartName, " +
                        "    p.Category, " +
                        "    pd.SKU, " +
                        "    pd.Quantity AS CurrentStock " +
                        "FROM DiagnosticPart dp " +
                        "JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID " +
                        "JOIN Part p ON pd.PartID = p.PartID " +
                        "WHERE dp.VehicleDiagnosticID = ? AND dp.IsApproved = 1 " +
                        "ORDER BY p.PartName";

        List<DiagnosticPart> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiagnosticPart(rs));
                }
            }
        }
        return list;
    }

    // ================================================================
    // UPDATE OPERATIONS
    // ================================================================

    /**
     * Approve một part trong diagnostic
     */
    public boolean approveDiagnosticPart(Connection conn, int diagnosticPartId)
            throws SQLException {
        String sql = "UPDATE DiagnosticPart SET IsApproved = 1 WHERE DiagnosticPartID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticPartId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Approve tất cả parts của một diagnostic
     */
    public boolean approveAllPartsInDiagnostic(Connection conn, int diagnosticId)
            throws SQLException {
        String sql = "UPDATE DiagnosticPart SET IsApproved = 1 " +
                "WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật thông tin part (quantity, price, condition, reason)
     *
     */
    public boolean updateDiagnosticPart(Connection conn, int diagnosticPartId,
                                        int quantityNeeded, BigDecimal unitPrice,
                                        String partCondition, String reason)
            throws SQLException {
        String sql = "UPDATE DiagnosticPart " +
                "SET QuantityNeeded = ?, UnitPrice = ?, " +
                "    PartCondition = ?, ReasonForReplacement = ? " +
                "WHERE DiagnosticPartID = ? AND IsApproved = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityNeeded);
            ps.setBigDecimal(2, unitPrice);
            ps.setString(3, partCondition);
            ps.setString(4, reason);
            ps.setInt(5, diagnosticPartId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // DELETE OPERATIONS
    // ================================================================

    /**
     * Delete part khỏi diagnostic (chỉ nếu chưa approve)
     */
    public boolean removePartFromDiagnostic(Connection conn, int diagnosticPartId)
            throws SQLException {
        String sql = "DELETE FROM DiagnosticPart " +
                "WHERE DiagnosticPartID = ? AND IsApproved = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticPartId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // CALCULATION & STATISTICS
    // ================================================================

    /**
     * Tính tổng estimate cost của parts trong diagnostic
     */
    public BigDecimal calculateTotalPartsCost(Connection conn, int diagnosticId)
            throws SQLException {
        String sql = "SELECT COALESCE(SUM(QuantityNeeded * UnitPrice), 0) AS TotalCost " +
                "FROM DiagnosticPart WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("TotalCost");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Đếm số parts theo condition trong một diagnostic
     */
    public int countPartsByCondition(Connection conn, int diagnosticId, String condition)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM DiagnosticPart " +
                "WHERE VehicleDiagnosticID = ? AND PartCondition = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setString(2, condition);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }
        return 0;
    }

    /**
     * Đếm số parts out of stock trong diagnostic
     */
    public int countOutOfStockParts(Connection conn, int diagnosticId)
            throws SQLException {
        String sql =
                "SELECT COUNT(*) AS cnt " +
                        "FROM DiagnosticPart dp " +
                        "JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID " +
                        "WHERE dp.VehicleDiagnosticID = ? " +
                        "  AND pd.Quantity < dp.QuantityNeeded";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }
        return 0;
    }

    // ================================================================
    // CONVERSION (Diagnostic → WorkOrder)
    // ================================================================

    /**
     * Chuyển approved diagnostic parts thành WorkOrderPart
     * (Khi Manager approve diagnostic)
     */
    public int convertToWorkOrderParts(Connection conn, int diagnosticId,
                                       int detailId, int requestedById)
            throws SQLException {
        String sql =
                "INSERT INTO WorkOrderPart " +
                        "(DetailID, PartDetailID, RequestedByID, QuantityUsed, UnitPrice, " +
                        "request_status, requested_at, source_diagnostic_part_id) " +
                        "SELECT " +
                        "    ?, " +
                        "    dp.PartDetailID, " +
                        "    ?, " +
                        "    dp.QuantityNeeded, " +
                        "    dp.UnitPrice, " +
                        "    'PENDING', " +
                        "    NOW(), " +
                        "    dp.DiagnosticPartID " +
                        "FROM DiagnosticPart dp " +
                        "WHERE dp.VehicleDiagnosticID = ? AND dp.IsApproved = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            ps.setInt(2, requestedById);
            ps.setInt(3, diagnosticId);
            return ps.executeUpdate();
        }
    }

    // ================================================================
    // HELPER - MAPPING METHOD
    // ================================================================

    /**
     * Map ResultSet sang DiagnosticPart object
     */
    private DiagnosticPart mapDiagnosticPart(ResultSet rs) throws SQLException {
        DiagnosticPart dp = new DiagnosticPart();

        dp.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
        dp.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
        dp.setPartDetailID(rs.getInt("PartDetailID"));
        dp.setQuantityNeeded(rs.getInt("QuantityNeeded"));
        dp.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        String cond = null;
        try { cond = rs.getString("PartCondition"); } catch (SQLException ignore) {}
        dp.setPartCondition(cond);
        dp.setReasonForReplacement(rs.getString("ReasonForReplacement"));
        dp.setApproved(rs.getBoolean("IsApproved"));

        // Thông tin Part (nếu có trong query)
        try {
            dp.setPartCode(rs.getString("PartCode"));
            dp.setPartName(rs.getString("PartName"));
            dp.setCategory(rs.getString("Category"));
            dp.setSku(rs.getString("SKU"));
            dp.setCurrentStock(rs.getInt("CurrentStock"));
        } catch (SQLException e) {
            // Columns không tồn tại - OK, skip
        }

        return dp;
    }
}